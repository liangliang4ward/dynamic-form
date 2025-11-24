package com.zjjg.digitize.validation;

import lombok.Data;

/**
 * 条件判断类，定义验证规则的执行条件
 */
@Data
public class Condition {
    private String conditionType;
    private String fieldName;
    private Object compareValue;
    private String relatedFieldName;
    private boolean crossFieldCondition;
}