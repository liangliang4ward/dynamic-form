package com.zjjg.digitize.validation.validators;

import com.zjjg.digitize.validation.ValidationResult;

import java.util.Map;

/**
 * 最小长度验证器
 * 验证字符串字段值的长度是否不小于指定的最小长度
 */
public class MinLengthValidator extends BaseValidator {

    public MinLengthValidator() {
        super("minLength");
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
                    "MIN_LENGTH_INVALID_TYPE",
                    String.format("Field '%s' must be a string", fieldName),
                    "validation.min.length.invalid.type",
                    params
            );
        }

        // 获取最小长度参数
        Integer minLength = getMinLengthParam(params);
        if (minLength == null) {
            return createFailureResult(
                    fieldName,
                    "MIN_LENGTH_PARAM_REQUIRED",
                    "Min length parameter is required",
                    "validation.min.length.param.required",
                    params
            );
        }

        String value = (String) fieldValue;
        if (value.length() < minLength) {
            return createFailureResult(
                    fieldName,
                    "MIN_LENGTH_NOT_MET",
                    String.format("Field '%s' must be at least %d characters long", fieldName, minLength),
                    "validation.min.length",
                    params
            );
        }

        return createSuccessResult();
    }

    /**
     * 获取最小长度参数
     * @param params 验证器参数
     * @return 最小长度
     */
    private Integer getMinLengthParam(Map<String, Object> params) {
        if (params == null) {
            return null;
        }

        Object minLengthObj = params.get("minLength");
        if (minLengthObj == null) {
            return null;
        }

        if (minLengthObj instanceof Integer) {
            return (Integer) minLengthObj;
        }

        if (minLengthObj instanceof String) {
            try {
                return Integer.parseInt((String) minLengthObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
