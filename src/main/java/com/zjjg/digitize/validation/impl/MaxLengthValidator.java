package com.zjjg.digitize.validation.impl;

import com.zjjg.digitize.validation.Validator;
import java.util.Map;

/**
 * 最大长度验证器
 * 验证字段值的长度是否不超过指定的最大值
 */
public class MaxLengthValidator implements Validator {
    
    @Override
    public String getType() {
        return "maxLength";
    }
    
    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, Map<String, Object> formData) {
        if (value == null) {
            return null; // 空值由必填验证器处理
        }
        if (!(value instanceof String)) {
            return "字段值必须是字符串";
        }
        
        Integer maxLength = (Integer) params.get("length");
        if (maxLength == null) {
            return "缺少最大长度参数";
        }
        
        String strValue = (String) value;
        if (strValue.length() > maxLength) {
            return "字段长度不能超过" + maxLength + "个字符";
        }
        return null;
    }
}
