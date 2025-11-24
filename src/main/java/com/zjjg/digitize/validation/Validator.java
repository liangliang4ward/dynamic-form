package com.zjjg.digitize.validation;

import com.alibaba.fastjson.JSONObject;
import java.util.Locale;

/**
 * 验证器接口，所有具体的验证器都需要实现这个接口
 */
public interface Validator {
    boolean validate(Object fieldValue, JSONObject params, JSONObject formData);
    String getDefaultErrorMessage(Locale locale);
    String getValidatorType();
}