package com.zjjg.digitize.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjg.digitize.validation.ValidationRule;
import lombok.Data;

import java.io.IOException;
import java.util.List;

/**
 * FormField entity representing the metadata for each field in a form
 */
@Data
@TableName("sys_form_field")
public class FormField {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long formId;
    private String fieldName;
    private String fieldType;
    private String fieldLabel;
    private Integer fieldLength;
    private boolean primaryKey;
    private boolean notNull;
    private String defaultValue;
    private Integer sortOrder;
    private String validationRulesJson;
    private List<ValidationRule> validationRules;

    public void setValidationRulesJson(String validationRulesJson) {
        this.validationRulesJson = validationRulesJson;
        if (validationRulesJson != null && !validationRulesJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.validationRules = objectMapper.readValue(validationRulesJson, new TypeReference<List<ValidationRule>>(){});
            } catch (IOException e) {
                throw new IllegalArgumentException("Invalid JSON format for validation rules", e);
            }
        }
    }

    public void setValidationRules(List<ValidationRule> validationRules) {
        this.validationRules = validationRules;
        if (validationRules != null && !validationRules.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.validationRulesJson = objectMapper.writeValueAsString(validationRules);
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to convert validation rules to JSON", e);
            }
        } else {
            this.validationRulesJson = null;
        }
    }
}