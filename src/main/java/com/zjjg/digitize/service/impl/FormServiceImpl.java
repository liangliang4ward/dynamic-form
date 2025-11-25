package com.zjjg.digitize.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.entity.FormField;
import com.zjjg.digitize.mapper.FormFieldMapper;
import com.zjjg.digitize.mapper.FormMapper;
import com.zjjg.digitize.service.FormService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FormServiceImpl extends ServiceImpl<FormMapper, Form> implements FormService {

    @Autowired
    private FormFieldMapper formFieldMapper;

    @Autowired
    private SqlSession sqlSession;

    @Override
    public boolean publishForm(Long formId) throws SQLException {
        Form form = getBaseMapper().selectById(formId);
        if (form == null) {
            throw new RuntimeException("Form not found with id: " + formId);
        }
        if ("published".equals(form.getStatus())) {
            return false; // Already published
        }
        List<FormField> fields = formFieldMapper.selectByFormId(formId);
        if (fields.isEmpty()) {
            throw new RuntimeException("Form has no fields, cannot publish");
        }
        String ddl = generateCreateTableDDL(form.getTableName(), fields);
        sqlSession.getConnection().prepareStatement(ddl).execute();
        // Update form status to published
        form.setStatus("published");
        getBaseMapper().updateById(form);
        return true;
    }

    private String generateCreateTableDDL(String tableName, List<FormField> fields) {
        StringBuilder ddl = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
        List<String> primaryKeys = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            FormField field = fields.get(i);
            ddl.append(field.getFieldName()).append(" ");
            String sqlType = mapFieldTypeToSqlType(field.getFieldType());
            ddl.append(sqlType);
            if ("VARCHAR".equals(sqlType) && field.getFieldLength() != null) {
                ddl.append("(").append(field.getFieldLength()).append(")");
            }
            if (field.isNotNull()) {
                ddl.append(" NOT NULL");
            }
            if (field.isPrimaryKey()) {
                primaryKeys.add(field.getFieldName());
            }
            if (i < fields.size() - 1) {
                ddl.append(", ");
            }
        }
        if (!primaryKeys.isEmpty()) {
            ddl.append(", PRIMARY KEY (");
            for (int i = 0; i < primaryKeys.size(); i++) {
                ddl.append(primaryKeys.get(i));
                if (i < primaryKeys.size() - 1) {
                    ddl.append(", ");
                }
            }
            ddl.append(")");
        }
        ddl.append(")");
        return ddl.toString();
    }

    private String mapFieldTypeToSqlType(String fieldType) {
        switch (fieldType.toLowerCase()) {
            case "string":
                return "VARCHAR";
            case "integer":
                return "INT";
            case "long":
                return "BIGINT";
            case "date":
                return "DATE";
            case "datetime":
                return "DATETIME";
            case "double":
                return "DOUBLE";
            default:
                throw new IllegalArgumentException("Unsupported field type: " + fieldType);
        }
    }
}