package com.zjjg.digitize.validation.validators;

import com.zjjg.digitize.validation.ValidationResult;

import java.util.Map;

/**
 * 最大长度验证器
 * 验证字符串字段值的长度是否不大于指定的最大长度
 */
public class MaxLengthValidator extends BaseValidator {

    public MaxLengthValidator() {
        super("maxLength");
    }

    @Override
    public ValidationResult validate(String fieldName, Object fieldValue, Map<String, Object> params, Map<String, Object> formData) {
        // 如果值为空，不进行验证（必填验证应该由RequiredValidator单独处理）
        if (isEmpty(fieldValue)) {
            return createSuccessResult();
        }

        if (!(fieldValue instanceof String)) {
            return createFailureResult(
                    fieldName,
                    "MAX_LENGTH_INVALID_TYPE",
                    String.format("Field '%s' must be a string", fieldName),
                    "validation.max.length.invalid.type",
                    params
            );
        }

        // 获取最大长度参数
        Integer maxLength = getMaxLengthParam(params);
        if (maxLength == null) {
            return createFailureResult(
                    fieldName,
                    "MAX_LENGTH_PARAM_REQUIRED",
                    "Max length parameter is required",
                    "validation.max.length.param.required",
                    params
            );
        }

        String value = (String) fieldValue;
        if (value.length() > maxLength) {
            return createFailureResult(
                    fieldName,
                    "MAX_LENGTH_EXCEEDED",
                    String.format("Field '%s' must be at most %d characters long", fieldName, maxLength),
                    "validation.max.length.exceeded",
                    params
            );
        }

        return createSuccessResult();
    }

    /**
     * 获取最大长度参数
     * @param params 验证器参数
     * @return 最大长度
     */
    private Integer getMaxLengthParam(Map<String, Object> params) {
        if (params == null) {
            return null;
        }

        Object maxLengthObj = params.get("maxLength");
        if (maxLengthObj == null) {
            return null;
        }

        if (maxLengthObj instanceof Integer) {
            return (Integer) maxLengthObj;
        }

        if (maxLengthObj instanceof String) {
            try {
                return Integer.parseInt((String) maxLengthObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
