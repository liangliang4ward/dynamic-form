package com.zjjg.digitize.validation.impl;

import com.zjjg.digitize.validation.Validator;
import java.util.Map;

/**
 * 最小长度验证器
 * 验证字段值的长度是否不小于指定的最小值
 */
public class MinLengthValidator implements Validator {
    
    @Override
    public String getType() {
        return "minLength";
    }
    
    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, Map<String, Object> formData) {
        if (value == null) {
            return null; // 空值由必填验证器处理
        }
        if (!(value instanceof String)) {
            return "字段值必须是字符串";
        }
        
        Integer minLength = (Integer) params.get("length");
        if (minLength == null) {
            return "缺少最小长度参数";
        }
        
        String strValue = (String) value;
        if (strValue.length() < minLength) {
            return "字段长度不能少于" + minLength + "个字符";
        }
        return null;
    }
}
