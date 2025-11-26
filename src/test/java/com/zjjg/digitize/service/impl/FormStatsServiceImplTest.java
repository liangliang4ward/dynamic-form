package com.zjjg.digitize.service.impl;

import com.zjjg.digitize.service.FormStatsService;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormStatsServiceImplTest {

    @Mock
    private SqlSession sqlSession;

    @InjectMocks
    private FormStatsServiceImpl formStatsService;

    @Test
    void testGetFormStats() throws SQLException {
        // Given
        String tableName = "test_table";

        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(conn.getMetaData()).thenReturn(metaData);

        ResultSet tableRs = mock(ResultSet.class);
        when(tableRs.next()).thenReturn(true);
        when(metaData.getTables(null, null, tableName.toUpperCase(), null)).thenReturn(tableRs);

        // Test total records
        PreparedStatement countPstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("SELECT COUNT(*) FROM " + tableName)).thenReturn(countPstmt);

        ResultSet countRs = mock(ResultSet.class);
        when(countRs.next()).thenReturn(true);
        when(countRs.getLong(1)).thenReturn(100L);
        when(countPstmt.executeQuery()).thenReturn(countRs);

        // Test create time statistics
        PreparedStatement timePstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("SELECT MIN(create_time), MAX(create_time) FROM " + tableName)).thenReturn(timePstmt);

        ResultSet timeRs = mock(ResultSet.class);
        when(timeRs.next()).thenReturn(true);
        Timestamp firstTime = Timestamp.valueOf("2023-01-01 00:00:00");
        Timestamp lastTime = Timestamp.valueOf("2023-12-31 23:59:59");
        when(timeRs.getTimestamp(1)).thenReturn(firstTime);
        when(timeRs.getTimestamp(2)).thenReturn(lastTime);
        when(timePstmt.executeQuery()).thenReturn(timeRs);

        // When
        Map<String, Object> stats = formStatsService.getFormStats(tableName);

        // Then
        assertNotNull(stats);
        assertEquals(100L, stats.get("totalRecords"));
        assertEquals(firstTime, stats.get("firstRecordTime"));
        assertEquals(lastTime, stats.get("lastRecordTime"));
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getTables(null, null, tableName.toUpperCase(), null);
        verify(conn, times(2)).prepareStatement(anyString());
        verify(countPstmt, times(1)).executeQuery();
        verify(timePstmt, times(1)).executeQuery();
    }

    @Test
    void testGetFieldStats() throws SQLException {
        // Given
        String tableName = "test_table";
        String fieldName = "test_field";

        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(conn.getMetaData()).thenReturn(metaData);

        // Test table exists
        ResultSet tableRs = mock(ResultSet.class);
        when(tableRs.next()).thenReturn(true);
        when(metaData.getTables(null, null, tableName.toUpperCase(), null)).thenReturn(tableRs);

        // Test field exists
        ResultSet fieldRs = mock(ResultSet.class);
        when(fieldRs.next()).thenReturn(true);
        when(metaData.getColumns(null, null, tableName.toUpperCase(), fieldName.toUpperCase())).thenReturn(fieldRs);

        // Test field type
        PreparedStatement fieldTypePstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?")).thenReturn(fieldTypePstmt);

        ResultSet fieldTypeRs = mock(ResultSet.class);
        when(fieldTypeRs.next()).thenReturn(true);
        when(fieldTypeRs.getString(1)).thenReturn("INT");
        when(fieldTypePstmt.executeQuery()).thenReturn(fieldTypeRs);

        // Test non-null count
        PreparedStatement nonNullPstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("SELECT COUNT(" + fieldName + ") FROM " + tableName)).thenReturn(nonNullPstmt);

        ResultSet nonNullRs = mock(ResultSet.class);
        when(nonNullRs.next()).thenReturn(true);
        when(nonNullRs.getLong(1)).thenReturn(90L);
        when(nonNullPstmt.executeQuery()).thenReturn(nonNullRs);

        // Test distinct count
        PreparedStatement distinctPstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("SELECT COUNT(DISTINCT " + fieldName + ") FROM " + tableName)).thenReturn(distinctPstmt);

        ResultSet distinctRs = mock(ResultSet.class);
        when(distinctRs.next()).thenReturn(true);
        when(distinctRs.getLong(1)).thenReturn(50L);
        when(distinctPstmt.executeQuery()).thenReturn(distinctRs);

        // Test min, max, avg for numeric field
        PreparedStatement minMaxAvgPstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("SELECT MIN(" + fieldName + "), MAX(" + fieldName + "), AVG(" + fieldName + ") FROM " + tableName)).thenReturn(minMaxAvgPstmt);

        ResultSet minMaxAvgRs = mock(ResultSet.class);
        when(minMaxAvgRs.next()).thenReturn(true);
        when(minMaxAvgRs.getObject(1)).thenReturn(1);
        when(minMaxAvgRs.getObject(2)).thenReturn(100);
        when(minMaxAvgRs.getObject(3)).thenReturn(50.5);
        when(minMaxAvgPstmt.executeQuery()).thenReturn(minMaxAvgRs);

        // When
        Map<String, Object> stats = formStatsService.getFieldStats(tableName, fieldName);

        // Then
        assertNotNull(stats);
        assertEquals("INT", stats.get("fieldType"));
        assertEquals(90L, stats.get("nonNullCount"));
        assertEquals(50L, stats.get("distinctCount"));
        assertEquals(1, stats.get("minValue"));
        assertEquals(100, stats.get("maxValue"));
        assertEquals(50.5, stats.get("avgValue"));
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getTables(null, null, tableName.toUpperCase(), null);
        verify(metaData, times(1)).getColumns(null, null, tableName.toUpperCase(), fieldName.toUpperCase());
        verify(conn, times(4)).prepareStatement(anyString());
        verify(fieldTypePstmt, times(1)).executeQuery();
        verify(nonNullPstmt, times(1)).executeQuery();
        verify(distinctPstmt, times(1)).executeQuery();
        verify(minMaxAvgPstmt, times(1)).executeQuery();
    }
}
