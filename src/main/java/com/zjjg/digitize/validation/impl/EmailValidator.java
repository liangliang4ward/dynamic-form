package com.zjjg.digitize.validation.impl;

import com.zjjg.digitize.validation.Validator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 邮箱验证器
 * 验证字段值是否为有效的邮箱格式
 */
public class EmailValidator implements Validator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    @Override
    public String getType() {
        return "email";
    }
    
    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, Map<String, Object> formData) {
        if (value == null) {
            return null; // 空值由必填验证器处理
        }
        if (!(value instanceof String)) {
            return "邮箱格式不正确";
        }
        String email = (String) value;
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "邮箱格式不正确";
        }
        return null;
    }
}
