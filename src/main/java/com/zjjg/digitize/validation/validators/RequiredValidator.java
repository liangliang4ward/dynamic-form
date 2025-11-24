package com.zjjg.digitize.validation.validators;

import com.zjjg.digitize.validation.ValidationResult;

import java.util.Map;

/**
 * 必填验证器
 * 验证字段值是否为空
 */
public class RequiredValidator extends BaseValidator {

    public RequiredValidator() {
        super("required");
    }

    @Override
    public ValidationResult validate(String fieldName, Object fieldValue, Map<String, Object> params, Map<String, Object> formData) {
        if (isEmpty(fieldValue)) {
            return createFailureResult(
                    fieldName,
                    "FIELD_REQUIRED",
                    String.format("Field '%s' is required", fieldName),
                    "validation.required",
                    params
            );
        }
        return createSuccessResult();
    }
}
