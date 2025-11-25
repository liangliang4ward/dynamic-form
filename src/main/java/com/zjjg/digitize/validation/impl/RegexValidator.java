package com.zjjg.digitize.validation.impl;

import com.zjjg.digitize.validation.Validator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 正则表达式验证器
 * 验证字段值是否符合指定的正则表达式
 */
public class RegexValidator implements Validator {
    
    @Override
    public String getType() {
        return "regex";
    }
    
    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, Map<String, Object> formData) {
        if (value == null) {
            return null; // 空值由必填验证器处理
        }
        if (!(value instanceof String)) {
            return "字段值必须是字符串";
        }
        
        String regex = (String) params.get("pattern");
        if (regex == null) {
            return "缺少正则表达式参数";
        }
        
        String strValue = (String) value;
        if (!Pattern.matches(regex, strValue)) {
            return "字段格式不正确";
        }
        return null;
    }
}
