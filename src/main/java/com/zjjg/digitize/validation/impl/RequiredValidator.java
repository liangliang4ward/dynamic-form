package com.zjjg.digitize.validation.impl;

import com.zjjg.digitize.validation.Validator;
import java.util.Map;

/**
 * 必填验证器
 * 验证字段值是否为空
 */
public class RequiredValidator implements Validator {
    
    @Override
    public String getType() {
        return "required";
    }
    
    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, Map<String, Object> formData) {
        if (value == null) {
            return "字段不能为空";
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            return "字段不能为空";
        }
        return null;
    }
}
