package com.zjjg.digitize.service.impl;

import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.entity.FormField;
import com.zjjg.digitize.mapper.FormFieldMapper;
import com.zjjg.digitize.mapper.FormMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormServiceImplTest {

    @Mock
    private FormMapper formMapper;

    @Mock
    private FormFieldMapper formFieldMapper;

    @Mock
    private SqlSession sqlSession;

    @InjectMocks
    private FormServiceImpl formService;

    // Mock the ServiceImpl's baseMapper
    static {
        try {
            java.lang.reflect.Field field = ServiceImpl.class.getDeclaredField("baseMapper");
            field.setAccessible(true);
            field.set(FormServiceImpl.class, FormMapper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPublishForm() throws SQLException {
        // Given
        Long formId = 1L;
        Form form = new Form();
        form.setId(formId);
        form.setTableName("test_table");
        form.setStatus("draft");
        
        List<FormField> fields = new ArrayList<>();
        FormField field1 = new FormField();
        field1.setFormId(formId);
        field1.setFieldName("id");
        field1.setFieldType("long");
        field1.setPrimaryKey(true);
        field1.setNotNull(true);
        fields.add(field1);
        
        FormField field2 = new FormField();
        field2.setFormId(formId);
        field2.setFieldName("name");
        field2.setFieldType("string");
        field2.setFieldLength(255);
        field2.setNotNull(true);
        fields.add(field2);
        
        when(formMapper.selectById(anyLong())).thenReturn(form);
        when(formFieldMapper.selectByFormId(anyLong())).thenReturn(fields);
        
        Connection conn = mock(Connection.class);
        when(sqlSession.getConnection()).thenReturn(conn);
        
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        
        // When
        boolean result = formService.publishForm(formId);
        
        // Then
        assertTrue(result);
        assertEquals("published", form.getStatus());
        verify(formMapper, times(1)).selectById(anyLong());
        verify(formFieldMapper, times(1)).selectByFormId(anyLong());
        verify(sqlSession, times(1)).getConnection();
        verify(conn, times(1)).prepareStatement(anyString());
        verify(pstmt, times(1)).execute();
        verify(formMapper, times(1)).updateById(any(Form.class));
    }

    @Test
    void testPublishForm_AlreadyPublished() throws SQLException {
        // Given
        Long formId = 1L;
        Form form = new Form();
        form.setId(formId);
        form.setStatus("published");
        
        when(formMapper.selectById(anyLong())).thenReturn(form);
        
        // When
        boolean result = formService.publishForm(formId);
        
        // Then
        assertFalse(result);
        verify(formMapper, times(1)).selectById(anyLong());
        verify(formFieldMapper, never()).selectByFormId(anyLong());
        verify(sqlSession, never()).getConnection();
    }

    @Test
    void testPublishForm_NoFields() throws SQLException {
        // Given
        Long formId = 1L;
        Form form = new Form();
        form.setId(formId);
        form.setStatus("draft");
        
        when(formMapper.selectById(anyLong())).thenReturn(form);
        when(formFieldMapper.selectByFormId(anyLong())).thenReturn(new ArrayList<>());
        
        // When
        Exception exception = assertThrows(RuntimeException.class, () -> {
            formService.publishForm(formId);
        });
        
        // Then
        assertEquals("Form has no fields, cannot publish", exception.getMessage());
        verify(formMapper, times(1)).selectById(anyLong());
        verify(formFieldMapper, times(1)).selectByFormId(anyLong());
        verify(sqlSession, never()).getConnection();
    }

    @Test
    void testPublishForm_FormNotFound() throws SQLException {
        // Given
        Long formId = 1L;
        
        when(formMapper.selectById(anyLong())).thenReturn(null);
        
        // When
        Exception exception = assertThrows(RuntimeException.class, () -> {
            formService.publishForm(formId);
        });
        
        // Then
        assertEquals("Form not found with id: " + formId, exception.getMessage());
        verify(formMapper, times(1)).selectById(anyLong());
        verify(formFieldMapper, never()).selectByFormId(anyLong());
        verify(sqlSession, never()).getConnection();
    }
}
