package com.zjjg.digitize.validation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 验证错误类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {

    /**
     * 错误字段名称
     */
    private String fieldName;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 错误信息国际化key
     */
    private String errorMessageKey;

    /**
     * 错误信息参数（用于国际化消息格式化）
     */
    private Map<String, Object> errorMessageParams;

    /**
     * 验证器类型
     */
    private String validatorType;

    /**
     * 创建验证错误对象
     * @param fieldName 错误字段名称
     * @param errorCode 错误代码
     * @param errorMessage 错误信息
     * @param validatorType 验证器类型
     * @return 验证错误对象
     */
    public static ValidationError create(String fieldName, String errorCode, String errorMessage, String validatorType) {
        return new ValidationError(fieldName, errorCode, errorMessage, null, null, validatorType);
    }

    /**
     * 创建验证错误对象（支持国际化）
     * @param fieldName 错误字段名称
     * @param errorCode 错误代码
     * @param errorMessage 错误信息
     * @param errorMessageKey 错误信息国际化key
     * @param errorMessageParams 错误信息参数
     * @param validatorType 验证器类型
     * @return 验证错误对象
     */
    public static ValidationError create(String fieldName, String errorCode, String errorMessage, String errorMessageKey, Map<String, Object> errorMessageParams, String validatorType) {
        return new ValidationError(fieldName, errorCode, errorMessage, errorMessageKey, errorMessageParams, validatorType);
    }
}
