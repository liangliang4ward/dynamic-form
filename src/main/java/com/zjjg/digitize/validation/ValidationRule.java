package com.zjjg.digitize.validation;

import com.alibaba.fastjson.JSONObject;
import java.util.List;

/**
 * 验证规则类，定义表单字段的验证规则
 */
public class ValidationRule {
    private String fieldName;
    private String validatorType;
    private JSONObject validatorParams;
    private String errorMessageKey;
    private List<Object> errorMessageParams;
    private Condition condition;
    private boolean crossFieldValidation;
    private String relatedFieldName;

    // getter和setter方法
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getValidatorType() { return validatorType; }
    public void setValidatorType(String validatorType) { this.validatorType = validatorType; }
    public JSONObject getValidatorParams() { return validatorParams; }
    public void setValidatorParams(JSONObject validatorParams) { this.validatorParams = validatorParams; }
    public String getErrorMessageKey() { return errorMessageKey; }
    public void setErrorMessageKey(String errorMessageKey) { this.errorMessageKey = errorMessageKey; }
    public List<Object> getErrorMessageParams() { return errorMessageParams; }
    public void setErrorMessageParams(List<Object> errorMessageParams) { this.errorMessageParams = errorMessageParams; }
    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }
    public boolean isCrossFieldValidation() { return crossFieldValidation; }
    public void setCrossFieldValidation(boolean crossFieldValidation) { this.crossFieldValidation = crossFieldValidation; }
    public String getRelatedFieldName() { return relatedFieldName; }
    public void setRelatedFieldName(String relatedFieldName) { this.relatedFieldName = relatedFieldName; }
}