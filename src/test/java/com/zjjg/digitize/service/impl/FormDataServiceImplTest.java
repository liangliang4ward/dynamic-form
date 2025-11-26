package com.zjjg.digitize.service.impl;

import com.zjjg.digitize.service.FormDataService;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormDataServiceImplTest {

    @Mock
    private SqlSession sqlSession;

    @InjectMocks
    private FormDataServiceImpl formDataService;

    @Test
    void testSaveFormData() throws SQLException {
        // Given
        String tableName = "test_table";
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Test Name");
        formData.put("value", "Test Value");

        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(conn.getMetaData()).thenReturn(metaData);

        ResultSet tableRs = mock(ResultSet.class);
        when(tableRs.next()).thenReturn(true);
        when(metaData.getTables(null, null, tableName.toUpperCase(), null)).thenReturn(tableRs);

        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(pstmt);

        when(pstmt.executeUpdate()).thenReturn(1);

        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getObject(1)).thenReturn(1);
        when(pstmt.getGeneratedKeys()).thenReturn(generatedKeys);

        // When
        Map<String, Object> result = formDataService.saveFormData(tableName, formData);

        // Then
        assertNotNull(result);
        assertEquals(1, result.get("id"));
        assertEquals("Test Name", result.get("name"));
        assertEquals("Test Value", result.get("value"));
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getTables(null, null, tableName.toUpperCase(), null);
        verify(conn, times(1)).prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS));
        verify(pstmt, times(1)).executeUpdate();
        verify(pstmt, times(1)).getGeneratedKeys();
    }

    @Test
    void testUpdateFormData() throws SQLException {
        // Given
        String tableName = "test_table";
        Object id = 1;
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", "Updated Name");
        formData.put("value", "Updated Value");

        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(conn.getMetaData()).thenReturn(metaData);

        ResultSet tableRs = mock(ResultSet.class);
        when(tableRs.next()).thenReturn(true);
        when(metaData.getTables(null, null, tableName.toUpperCase(), null)).thenReturn(tableRs);

        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        when(pstmt.executeUpdate()).thenReturn(1);

        // When
        Map<String, Object> result = formDataService.updateFormData(tableName, id, formData);

        // Then
        assertNotNull(result);
        assertEquals(id, result.get("id"));
        assertEquals("Updated Name", result.get("name"));
        assertEquals("Updated Value", result.get("value"));
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getTables(null, null, tableName.toUpperCase(), null);
        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeUpdate();
    }

    @Test
    void testGetFormDataById() throws SQLException {
        // Given
        String tableName = "test_table";
        Object id = 1;

        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(conn.getMetaData()).thenReturn(metaData);

        ResultSet tableRs = mock(ResultSet.class);
        when(tableRs.next()).thenReturn(true);
        when(metaData.getTables(null, null, tableName.toUpperCase(), null)).thenReturn(tableRs);

        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getObject("id")).thenReturn(id);
        when(rs.getObject("name")).thenReturn("Test Name");
        when(rs.getObject("value")).thenReturn("Test Value");
        when(pstmt.executeQuery()).thenReturn(rs);

        ResultSetMetaData rsMetaData = mock(ResultSetMetaData.class);
        when(rs.getMetaData()).thenReturn(rsMetaData);
        when(rsMetaData.getColumnCount()).thenReturn(3);
        when(rsMetaData.getColumnName(1)).thenReturn("id");
        when(rsMetaData.getColumnName(2)).thenReturn("name");
        when(rsMetaData.getColumnName(3)).thenReturn("value");

        // When
        Map<String, Object> result = formDataService.getFormDataById(tableName, id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.get("id"));
        assertEquals("Test Name", result.get("name"));
        assertEquals("Test Value", result.get("value"));
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getTables(null, null, tableName.toUpperCase(), null);
        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeQuery();
    }

    @Test
    void testDeleteFormDataById() throws SQLException {
        // Given
        String tableName = "test_table";
        Object id = 1;

        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(conn.getMetaData()).thenReturn(metaData);

        ResultSet tableRs = mock(ResultSet.class);
        when(tableRs.next()).thenReturn(true);
        when(metaData.getTables(null, null, tableName.toUpperCase(), null)).thenReturn(tableRs);

        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        when(pstmt.executeUpdate()).thenReturn(1);

        // When
        boolean result = formDataService.deleteFormDataById(tableName, id);

        // Then
        assertTrue(result);
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getTables(null, null, tableName.toUpperCase(), null);
        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeUpdate();
    }

    @Test
    void testListFormData() throws SQLException {
        // Given
        String tableName = "test_table";
        int page = 1;
        int size = 10;

        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);

        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(conn.getMetaData()).thenReturn(metaData);

        ResultSet tableRs = mock(ResultSet.class);
        when(tableRs.next()).thenReturn(true);
        when(metaData.getTables(null, null, tableName.toUpperCase(), null)).thenReturn(tableRs);

        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getObject("id")).thenReturn(1);
        when(rs.getObject("name")).thenReturn("Test Name");
        when(rs.getObject("value")).thenReturn("Test Value");
        when(pstmt.executeQuery()).thenReturn(rs);

        ResultSetMetaData rsMetaData = mock(ResultSetMetaData.class);
        when(rs.getMetaData()).thenReturn(rsMetaData);
        when(rsMetaData.getColumnCount()).thenReturn(3);
        when(rsMetaData.getColumnName(1)).thenReturn("id");
        when(rsMetaData.getColumnName(2)).thenReturn("name");
        when(rsMetaData.getColumnName(3)).thenReturn("value");

        // When
        List<Map<String, Object>> result = formDataService.listFormData(tableName, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, Object> data = result.get(0);
        assertEquals(1, data.get("id"));
        assertEquals("Test Name", data.get("name"));
        assertEquals("Test Value", data.get("value"));
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).getMetaData();
        verify(metaData, times(1)).getTables(null, null, tableName.toUpperCase(), null);
        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).executeQuery();
    }
}
