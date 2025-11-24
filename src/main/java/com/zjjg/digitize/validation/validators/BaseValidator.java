package com.zjjg.digitize.validation.validators;

import com.zjjg.digitize.validation.ValidationError;
import com.zjjg.digitize.validation.ValidationResult;
import com.zjjg.digitize.validation.Validator;

import java.util.Map;

/**
 * 基础验证器实现
 * 提供一些通用的功能，作为其他验证器的父类
 */
public abstract class BaseValidator implements Validator {

    /**
     * 验证器类型
     */
    private String type;

    /**
     * 构造函数
     * @param type 验证器类型
     */
    public BaseValidator(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    /**
     * 创建验证失败的结果
     * @param fieldName 字段名称
     * @param errorCode 错误代码
     * @param errorMessage 错误信息
     * @return 验证失败的结果
     */
    protected ValidationResult createFailureResult(String fieldName, String errorCode, String errorMessage) {
        ValidationError error = ValidationError.create(fieldName, errorCode, errorMessage, getType());
        return ValidationResult.failure(error);
    }

    /**
     * 创建验证失败的结果（支持国际化）
     * @param fieldName 字段名称
     * @param errorCode 错误代码
     * @param errorMessage 错误信息
     * @param errorMessageKey 错误信息国际化key
     * @param errorMessageParams 错误信息参数
     * @return 验证失败的结果
     */
    protected ValidationResult createFailureResult(String fieldName, String errorCode, String errorMessage, String errorMessageKey, Map<String, Object> errorMessageParams) {
        ValidationError error = ValidationError.create(fieldName, errorCode, errorMessage, errorMessageKey, errorMessageParams, getType());
        return ValidationResult.failure(error);
    }

    /**
     * 创建验证通过的结果
     * @return 验证通过的结果
     */
    protected ValidationResult createSuccessResult() {
        return ValidationResult.success();
    }

    /**
     * 检查值是否为空
     * @param value 值
     * @return 是否为空
     */
    protected boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        if (value instanceof java.util.Collection) {
            return ((java.util.Collection<?>) value).isEmpty();
        }
        if (value instanceof java.util.Map) {
            return ((java.util.Map<?, ?>) value).isEmpty();
        }
        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value) == 0;
        }
        return false;
    }

    /**
     * 检查值是否不为空
     * @param value 值
     * @return 是否不为空
     */
    protected boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }
}
