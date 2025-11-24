package com.zjjg.digitize.validation;

/**
 * 条件判断类，定义验证规则的执行条件
 */
public class Condition {
    private String conditionType;
    private String fieldName;
    private Object compareValue;
    private String relatedFieldName;
    private boolean crossFieldCondition;

    // getter和setter方法
    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public Object getCompareValue() { return compareValue; }
    public void setCompareValue(Object compareValue) { this.compareValue = compareValue; }
    public String getRelatedFieldName() { return relatedFieldName; }
    public void setRelatedFieldName(String relatedFieldName) { this.relatedFieldName = relatedFieldName; }
    public boolean isCrossFieldCondition() { return crossFieldCondition; }
    public void setCrossFieldCondition(boolean crossFieldCondition) { this.crossFieldCondition = crossFieldCondition; }
}