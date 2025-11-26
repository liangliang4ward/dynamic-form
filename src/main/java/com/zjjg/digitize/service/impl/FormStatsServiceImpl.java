package com.zjjg.digitize.service.impl;

import com.zjjg.digitize.service.FormStatsService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of FormStatsService
 */
@Service
public class FormStatsServiceImpl implements FormStatsService {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public Map<String, Object> getFormStats(String tableName) {
        // Validate table exists
        if (!tableExists(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        // Get connection
        try (Connection conn = sqlSession.getConnection()) {
            Map<String, Object> stats = new HashMap<>();
            
            // Get total records
            String countSql = "SELECT COUNT(*) FROM " + tableName;
            try (PreparedStatement pstmt = conn.prepareStatement(countSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalRecords", rs.getLong(1));
                }
            }
            
            // Get create time statistics
            String timeSql = "SELECT MIN(create_time), MAX(create_time) FROM " + tableName;
            try (PreparedStatement pstmt = conn.prepareStatement(timeSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("firstRecordTime", rs.getTimestamp(1));
                    stats.put("lastRecordTime", rs.getTimestamp(2));
                }
            }
            
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get form statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getFieldStats(String tableName, String fieldName) {
        // Validate table exists
        if (!tableExists(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        // Validate field exists
        if (!fieldExists(tableName, fieldName)) {
            throw new RuntimeException("Field not found: " + fieldName);
        }
        // Get connection
        try (Connection conn = sqlSession.getConnection()) {
            Map<String, Object> stats = new HashMap<>();
            
            // Get field type
            String fieldTypeSql = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(fieldTypeSql)) {
                pstmt.setString(1, tableName);
                pstmt.setString(2, fieldName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("fieldType", rs.getString(1));
                    }
                }
            }
            
            // Get non-null count
            String nonNullCountSql = "SELECT COUNT(" + fieldName + ") FROM " + tableName;
            try (PreparedStatement pstmt = conn.prepareStatement(nonNullCountSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("nonNullCount", rs.getLong(1));
                }
            }
            
            // Get distinct count
            String distinctCountSql = "SELECT COUNT(DISTINCT " + fieldName + ") FROM " + tableName;
            try (PreparedStatement pstmt = conn.prepareStatement(distinctCountSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("distinctCount", rs.getLong(1));
                }
            }
            
            // Get min, max, avg for numeric fields
            String fieldType = (String) stats.get("fieldType");
            if (fieldType != null && (fieldType.contains("INT") || fieldType.contains("NUMERIC") || fieldType.contains("DOUBLE"))) {
                String minMaxAvgSql = "SELECT MIN(" + fieldName + "), MAX(" + fieldName + "), AVG(" + fieldName + ") FROM " + tableName;
                try (PreparedStatement pstmt = conn.prepareStatement(minMaxAvgSql);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("minValue", rs.getObject(1));
                        stats.put("maxValue", rs.getObject(2));
                        stats.put("avgValue", rs.getObject(3));
                    }
                }
            }
            
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get field statistics: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a table exists in the database
     * @param tableName table name
     * @return boolean indicating existence
     */
    private boolean tableExists(String tableName) {
        try (Connection conn = sqlSession.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), null)) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check table existence: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a field exists in a table
     * @param tableName table name
     * @param fieldName field name
     * @return boolean indicating existence
     */
    private boolean fieldExists(String tableName, String fieldName) {
        try (Connection conn = sqlSession.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, null, tableName.toUpperCase(), fieldName.toUpperCase())) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check field existence: " + e.getMessage(), e);
        }
    }
}