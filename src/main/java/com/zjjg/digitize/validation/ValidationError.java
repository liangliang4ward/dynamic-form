package com.zjjg.digitize.validation;

/**
 * 验证错误类，封装单个字段的验证错误信息
 */
public class ValidationError {
    private String fieldName;
    private String errorMessage;
    private String errorMessageKey;
    private Object[] errorMessageParams;

    public ValidationError(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public ValidationError(String fieldName, String errorMessage, String errorMessageKey, Object[] errorMessageParams) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
        this.errorMessageKey = errorMessageKey;
        this.errorMessageParams = errorMessageParams;
    }

    // getter和setter方法
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getErrorMessageKey() { return errorMessageKey; }
    public void setErrorMessageKey(String errorMessageKey) { this.errorMessageKey = errorMessageKey; }
    public Object[] getErrorMessageParams() { return errorMessageParams; }
    public void setErrorMessageParams(Object[] errorMessageParams) { this.errorMessageParams = errorMessageParams; }
}