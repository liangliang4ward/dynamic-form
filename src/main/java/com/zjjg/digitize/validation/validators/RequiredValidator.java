package com.zjjg.digitize.validation.validators;

import com.alibaba.fastjson.JSONObject;
import com.zjjg.digitize.validation.Validator;
import java.util.Locale;

/**
 * 必填验证器，验证字段是否必填（样例验证器）
 */
public class RequiredValidator implements Validator {

    @Override
    public boolean validate(Object fieldValue, JSONObject params, JSONObject formData) {
        if (fieldValue == null) {
            return false;
        }

        if (fieldValue instanceof String) {
            return !((String) fieldValue).trim().isEmpty();
        }

        return true;
    }

    @Override
    public String getDefaultErrorMessage(Locale locale) {
        if (Locale.US.equals(locale)) {
            return "This field is required";
        } else if (Locale.CHINA.equals(locale)) {
            return "此字段为必填项";
        }
        return "This field is required";
    }

    @Override
    public String getValidatorType() {
        return "required";
    }
}