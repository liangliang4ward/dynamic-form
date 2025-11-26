package com.zjjg.digitize.service.impl;

import com.zjjg.digitize.service.FormDataService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of FormDataService
 */
@Service
@Transactional
public class FormDataServiceImpl implements FormDataService {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public Map<String, Object> saveFormData(String tableName, Map<String, Object> formData) {
        // Validate table exists
        if (!tableExists(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        // Get connection
        try (Connection conn = sqlSession.getConnection()) {
            // Build insert SQL
            StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
            StringBuilder values = new StringBuilder("VALUES (");
            List<Object> params = new ArrayList<>();
            
            int i = 0;
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                if (i > 0) {
                    sql.append(", ");
                    values.append(", ");
                }
                sql.append(entry.getKey());
                values.append("?");
                params.add(entry.getValue());
                i++;
            }
            sql.append(") ");
            values.append(")");
            sql.append(values);
            
            // Execute insert
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                for (int j = 0; j < params.size(); j++) {
                    pstmt.setObject(j + 1, params.get(j));
                }
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating record failed, no rows affected.");
                }
                
                // Get generated key
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        formData.put("id", generatedKeys.getObject(1));
                    } else {
                        throw new SQLException("Creating record failed, no generated key obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save form data: " + e.getMessage(), e);
        }
        return formData;
    }

    @Override
    public Map<String, Object> updateFormData(String tableName, Object id, Map<String, Object> formData) {
        // Validate table exists
        if (!tableExists(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        // Get connection
        try (Connection conn = sqlSession.getConnection()) {
            // Build update SQL
            StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            List<Object> params = new ArrayList<>();
            
            int i = 0;
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                if ("id".equals(entry.getKey())) {
                    continue; // Skip id field
                }
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(entry.getKey()).append(" = ?");
                params.add(entry.getValue());
                i++;
            }
            sql.append(" WHERE id = ?");
            params.add(id);
            
            // Execute update
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int j = 0; j < params.size(); j++) {
                    pstmt.setObject(j + 1, params.get(j));
                }
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new RuntimeException("Updating record failed, no rows affected.");
                }
                
                // Return updated data
                formData.put("id", id);
                return formData;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update form data: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getFormDataById(String tableName, Object id) {
        // Validate table exists
        if (!tableExists(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        // Get connection
        try (Connection conn = sqlSession.getConnection()) {
            // Build select SQL
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            
            // Execute select
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return resultSetToMap(rs);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get form data: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFormDataById(String tableName, Object id) {
        // Validate table exists
        if (!tableExists(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        // Get connection
        try (Connection conn = sqlSession.getConnection()) {
            // Build delete SQL
            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            
            // Execute delete
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, id);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete form data: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> listFormData(String tableName, int page, int size) {
        // Validate table exists
        if (!tableExists(tableName)) {
            throw new RuntimeException("Table not found: " + tableName);
        }
        // Calculate offset
        int offset = (page - 1) * size;
        // Get connection
        try (Connection conn = sqlSession.getConnection()) {
            // Build select SQL with pagination
            String sql = "SELECT * FROM " + tableName + " LIMIT ? OFFSET ?";
            
            // Execute select
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, size);
                pstmt.setInt(2, offset);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Map<String, Object>> resultList = new ArrayList<>();
                    while (rs.next()) {
                        resultList.add(resultSetToMap(rs));
                    }
                    return resultList;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list form data: " + e.getMessage(), e);
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
     * Convert ResultSet to Map
     * @param rs ResultSet
     * @return Map of column names to values
     * @throws SQLException if there's an error converting ResultSet
     */
    private Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object value = rs.getObject(i);
            map.put(columnName, value);
        }
        return map;
    }
}