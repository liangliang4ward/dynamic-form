package com.zjjg.digitize.validation;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证规则类，定义表单字段的验证规则
 */
@Data
public class ValidationRule {
    private String fieldName;
    private String validatorType;
    private JSONObject validatorParams;
    private String errorMessageKey;
    private List<Object> errorMessageParams = new ArrayList<>();
    private Condition condition;
    private boolean crossFieldValidation;
    private String relatedFieldName;

}