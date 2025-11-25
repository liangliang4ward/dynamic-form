package com.zjjg.digitize.validation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 验证引擎核心类
 * 负责解析验证规则并执行验证
 */
@Component
public class ValidationEngine implements ApplicationContextAware {
    
    /**
     * 验证器映射，key为验证类型，value为验证器实例
     */
    private Map<String, Validator> validatorMap = new HashMap<>();
    
    /**
     * 初始化验证器映射
     * @param applicationContext 应用上下文
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取所有Validator接口的实现类
        Map<String, Validator> validators = applicationContext.getBeansOfType(Validator.class);
        // 将验证器按类型存入映射
        validators.values().forEach(validator -> validatorMap.put(validator.getType(), validator));
    }
    
    /**
     * 解析JSON格式的验证规则
     * @param jsonConfig JSON配置
     * @return 验证规则映射，key为字段名，value为验证规则列表
     */
    public Map<String, List<ValidationRule>> parseRules(String jsonConfig) {
        JSONObject jsonObject = JSON.parseObject(jsonConfig);
        return jsonObject.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> JSON.parseArray(entry.getValue().toString(), ValidationRule.class)
                ));
    }
    
    /**
     * 执行验证
     * @param rules 验证规则映射
     * @param formData 表单数据
     * @return 验证结果
     */
    public ValidationResult validate(Map<String, List<ValidationRule>> rules, Map<String, Object> formData) {
        ValidationResult result = new ValidationResult();
        
        if (rules == null || rules.isEmpty()) {
            return result; // 没有验证规则，直接通过
        }
        
        // 遍历每个字段的验证规则
        for (Map.Entry<String, List<ValidationRule>> entry : rules.entrySet()) {
            String fieldName = entry.getKey();
            List<ValidationRule> fieldRules = entry.getValue();
            Object fieldValue = formData.get(fieldName);
            
            // 遍历该字段的每个验证规则
            for (ValidationRule rule : fieldRules) {
                // 检查验证条件是否满足
                if (rule.getCondition() != null && !isConditionSatisfied(rule.getCondition(), formData)) {
                    continue; // 条件不满足，跳过该验证规则
                }
                
                // 获取对应的验证器
                Validator validator = validatorMap.get(rule.getType());
                if (validator == null) {
                    result.addError(fieldName, "未知的验证类型：" + rule.getType());
                    continue;
                }
                
                // 执行验证
                String errorMessage = validator.validate(fieldName, fieldValue, rule.getParams(), formData);
                if (errorMessage != null) {
                    // 如果规则中配置了错误信息，则使用配置的错误信息，否则使用验证器返回的错误信息
                    String finalMessage = rule.getMessage() != null ? rule.getMessage() : errorMessage;
                    result.addError(fieldName, finalMessage);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 检查条件是否满足
     * @param condition 条件
     * @param formData 表单数据
     * @return 是否满足条件
     */
    private boolean isConditionSatisfied(Condition condition, Map<String, Object> formData) {
        if (condition == null) {
            return true;
        }
        
        switch (condition.getType()) {
            case "single":
                return isSingleConditionSatisfied(condition.getSingle(), formData);
            case "and":
                return condition.getConditions().stream()
                        .allMatch(c -> isConditionSatisfied(c, formData));
            case "or":
                return condition.getConditions().stream()
                        .anyMatch(c -> isConditionSatisfied(c, formData));
            default:
                return false;
        }
    }
    
    /**
     * 检查单条件是否满足
     * @param singleCondition 单条件
     * @param formData 表单数据
     * @return 是否满足条件
     */
    private boolean isSingleConditionSatisfied(Condition.SingleCondition singleCondition, Map<String, Object> formData) {
        if (singleCondition == null) {
            return false;
        }
        
        String field = singleCondition.getField();
        String operator = singleCondition.getOperator();
        Object value = singleCondition.getValue();
        
        Object fieldValue = formData.get(field);
        
        switch (operator) {
            case "eq":
                return equals(fieldValue, value);
            case "ne":
                return !equals(fieldValue, value);
            case "gt":
                return compare(fieldValue, value) > 0;
            case "lt":
                return compare(fieldValue, value) < 0;
            case "ge":
                return compare(fieldValue, value) >= 0;
            case "le":
                return compare(fieldValue, value) <= 0;
            case "contains":
                return contains(fieldValue, value);
            case "empty":
                return isEmpty(fieldValue);
            case "notEmpty":
                return !isEmpty(fieldValue);
            default:
                return false;
        }
    }
    
    /**
     * 比较两个值的大小
     * @param a 值a
     * @param b 值b
     * @return 比较结果
     */
    private int compare(Object a, Object b) {
        if (a == null || b == null) {
            return 0;
        }
        
        if (a instanceof Comparable && b instanceof Comparable) {
            try {
                return ((Comparable) a).compareTo(b);
            } catch (ClassCastException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * 判断两个值是否相等
     * @param a 值a
     * @param b 值b
     * @return 是否相等
     */
    private boolean equals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }
    
    /**
     * 判断值是否包含指定内容
     * @param a 值a
     * @param b 值b
     * @return 是否包含
     */
    private boolean contains(Object a, Object b) {
        if (a == null || b == null) {
            return false;
        }
        
        if (a instanceof String && b instanceof String) {
            return ((String) a).contains((String) b);
        }
        
        if (a instanceof List && b != null) {
            return ((List) a).contains(b);
        }
        
        return false;
    }
    
    /**
     * 判断值是否为空
     * @param value 值
     * @return 是否为空
     */
    private boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            return true;
        }
        if (value instanceof List && ((List) value).isEmpty()) {
            return true;
        }
        if (value instanceof Map && ((Map) value).isEmpty()) {
            return true;
        }
        return false;
    }
}
