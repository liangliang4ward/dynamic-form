package com.zjjg.digitize.validation.validators;

import com.zjjg.digitize.validation.ValidationResult;

import java.util.Map;

/**
 * 数值范围验证器
 * 验证数值字段值是否在指定的范围内（包括最小值和最大值）
 */
public class RangeValidator extends BaseValidator {

    public RangeValidator() {
        super("range");
    }

    @Override
    public ValidationResult validate(String fieldName, Object fieldValue, Map<String, Object> params, Map<String, Object> formData) {
        // 如果值为空，不进行验证（必填验证应该由RequiredValidator单独处理）
        if (isEmpty(fieldValue)) {
            return createSuccessResult();
        }

        // 转换为数值类型
        Number numberValue = convertToNumber(fieldValue);
        if (numberValue == null) {
            return createFailureResult(
                    fieldName,
                    "RANGE_INVALID_TYPE",
                    String.format("Field '%s' must be a number", fieldName),
                    "validation.range.invalid.type",
                    params
            );
        }

        // 获取范围参数
        Double min = getParamAsDouble(params, "min");
        Double max = getParamAsDouble(params, "max");

        if (min == null && max == null) {
            return createFailureResult(
                    fieldName,
                    "RANGE_PARAM_REQUIRED",
                    "At least one of min or max parameters is required",
                    "validation.range.param.required",
                    params
            );
        }

        double value = numberValue.doubleValue();

        if (min != null && value < min) {
            return createFailureResult(
                    fieldName,
                    "RANGE_BELOW_MIN",
                    String.format("Field '%s' must be at least %.2f", fieldName, min),
                    "validation.range.min",
                    params
            );
        }

        if (max != null && value > max) {
            return createFailureResult(
                    fieldName,
                    "RANGE_ABOVE_MAX",
                    String.format("Field '%s' must be at most %.2f", fieldName, max),
                    "validation.range.max",
                    params
            );
        }

        return createSuccessResult();
    }

    /**
     * 将值转换为数值类型
     * @param value 值
     * @return 数值类型
     */
    private Number convertToNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        }

        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * 获取参数作为Double类型
     * @param params 验证器参数
     * @param paramName 参数名称
     * @return Double类型的参数值
     */
    private Double getParamAsDouble(Map<String, Object> params, String paramName) {
        if (params == null) {
            return null;
        }

        Object paramObj = params.get(paramName);
        if (paramObj == null) {
            return null;
        }

        if (paramObj instanceof Number) {
            return ((Number) paramObj).doubleValue();
        }

        if (paramObj instanceof String) {
            try {
                return Double.parseDouble((String) paramObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
