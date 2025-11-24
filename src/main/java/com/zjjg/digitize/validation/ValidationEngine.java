package com.zjjg.digitize.validation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjjg.digitize.validation.validators.RequiredValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证引擎核心类，负责解析和执行验证规则
 */
@Component
public class ValidationEngine {
    private final Map<String, Validator> validatorRegistry = new ConcurrentHashMap<>();
    private MessageSource messageSource;
    private Locale defaultLocale = Locale.CHINA;

    public ValidationEngine() {
        registerValidator(new RequiredValidator());
    }

    public void registerValidator(Validator validator) {
        validatorRegistry.put(validator.getValidatorType(), validator);
    }

    public List<ValidationRule> parseRules(JSONArray jsonRules) {
        List<ValidationRule> rules = new ArrayList<>();
        if (jsonRules == null || jsonRules.isEmpty()) {
            return rules;
        }

        for (Object obj : jsonRules) {
            JSONObject jsonRule = (JSONObject) obj;
            ValidationRule rule = new ValidationRule();
            rule.setFieldName(jsonRule.getString("fieldName"));
            rule.setValidatorType(jsonRule.getString("validatorType"));
            rule.setValidatorParams(jsonRule.getJSONObject("validatorParams"));
            rule.setErrorMessageKey(jsonRule.getString("errorMessageKey"));
            rule.setErrorMessageParams(jsonRule.getJSONArray("errorMessageParams"));
            rule.setCrossFieldValidation(jsonRule.getBooleanValue("crossFieldValidation"));
            rule.setRelatedFieldName(jsonRule.getString("relatedFieldName"));

            JSONObject jsonCondition = jsonRule.getJSONObject("condition");
            if (jsonCondition != null) {
                Condition condition = new Condition();
                condition.setConditionType(jsonCondition.getString("conditionType"));
                condition.setFieldName(jsonCondition.getString("fieldName"));
                condition.setCompareValue(jsonCondition.get("compareValue"));
                condition.setRelatedFieldName(jsonCondition.getString("relatedFieldName"));
                condition.setCrossFieldCondition(jsonCondition.getBooleanValue("crossFieldCondition"));
                rule.setCondition(condition);
            }

            rules.add(rule);
        }

        return rules;
    }

    public ValidationResult validate(JSONObject formData, List<ValidationRule> rules, Locale locale) {
        ValidationResult result = new ValidationResult();
        if (rules == null || rules.isEmpty()) {
            return result;
        }

        for (ValidationRule rule : rules) {
            if (!isConditionSatisfied(rule.getCondition(), formData)) {
                continue;
            }
            if(rule.getErrorMessageParams()==null){
                rule.setErrorMessageParams(new ArrayList<>());
            }

            Validator validator = validatorRegistry.get(rule.getValidatorType());
            if (validator == null) {
                String errorMessage = getLocalizedMessage("validator.unknown", new Object[]{rule.getValidatorType()}, 
                    "未知的验证器类型: " + rule.getValidatorType(), locale);
                result.addError(new ValidationError(rule.getFieldName(), errorMessage));
                continue;
            }

            Object fieldValue = formData.get(rule.getFieldName());
            boolean isValid = validator.validate(fieldValue, rule.getValidatorParams(), formData);
            if (!isValid) {
                String errorMessage = getErrorMessage(rule, validator, locale);
                result.addError(new ValidationError(rule.getFieldName(), errorMessage, 
                    rule.getErrorMessageKey(), rule.getErrorMessageParams().toArray()));
            }
        }

        return result;
    }

    private boolean isConditionSatisfied(Condition condition, JSONObject formData) {
        if (condition == null) {
            return true;
        }

        Object fieldValue = formData.get(condition.getFieldName());
        Object compareValue = condition.isCrossFieldCondition() ? 
            formData.get(condition.getRelatedFieldName()) : condition.getCompareValue();

        if (fieldValue == null || compareValue == null) {
            return false;
        }

        if (fieldValue instanceof String && compareValue instanceof String) {
            return compareStringValues((String) fieldValue, (String) compareValue, condition.getConditionType());
        } else if (fieldValue instanceof Number && compareValue instanceof Number) {
            return compareNumberValues((Number) fieldValue, (Number) compareValue, condition.getConditionType());
        } else if (fieldValue instanceof Boolean && compareValue instanceof Boolean) {
            return compareBooleanValues((Boolean) fieldValue, (Boolean) compareValue, condition.getConditionType());
        }

        return false;
    }

    private boolean compareStringValues(String fieldValue, String compareValue, String conditionType) {
        switch (conditionType) {
            case "equals": return fieldValue.equals(compareValue);
            case "notEquals": return !fieldValue.equals(compareValue);
            case "contains": return fieldValue.contains(compareValue);
            default: return false;
        }
    }

    private boolean compareNumberValues(Number fieldValue, Number compareValue, String conditionType) {
        double fieldDouble = fieldValue.doubleValue();
        double compareDouble = compareValue.doubleValue();

        switch (conditionType) {
            case "equals": return fieldDouble == compareDouble;
            case "notEquals": return fieldDouble != compareDouble;
            case "greaterThan": return fieldDouble > compareDouble;
            case "lessThan": return fieldDouble < compareDouble;
            default: return false;
        }
    }

    private boolean compareBooleanValues(Boolean fieldValue, Boolean compareValue, String conditionType) {
        switch (conditionType) {
            case "equals": return fieldValue.equals(compareValue);
            case "notEquals": return !fieldValue.equals(compareValue);
            default: return false;
        }
    }

    private String getErrorMessage(ValidationRule rule, Validator validator, Locale locale) {
        if (rule.getErrorMessageKey() != null && messageSource != null) {
            try {
                return messageSource.getMessage(rule.getErrorMessageKey(), 
                    rule.getErrorMessageParams().toArray(), locale);
            } catch (NoSuchMessageException e) {
                return validator.getDefaultErrorMessage(locale);
            }
        }
        return validator.getDefaultErrorMessage(locale);
    }

    private String getLocalizedMessage(String messageKey, Object[] params, String defaultMessage, Locale locale) {
        if (messageSource != null) {
            try {
                return messageSource.getMessage(messageKey, params, locale);
            } catch (NoSuchMessageException e) {
                return defaultMessage;
            }
        }
        return defaultMessage;
    }

    public void setMessageSource(MessageSource messageSource) { this.messageSource = messageSource; }
    public void setDefaultLocale(Locale defaultLocale) { this.defaultLocale = defaultLocale; }
}