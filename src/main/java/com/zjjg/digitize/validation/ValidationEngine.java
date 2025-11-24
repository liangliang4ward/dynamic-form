package com.zjjg.digitize.validation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjjg.digitize.i18n.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 验证引擎类
 * 负责解析验证规则并执行验证
 */
@Slf4j
@Component
public class ValidationEngine {

    /**
     * 验证器映射表，key为验证器类型，value为验证器实例
     */
    private Map<String, Validator> validatorMap = new HashMap<>();



    /**
     * 构造函数，自动注入所有验证器实例
     * @param validators 验证器实例列表
     */
    @Autowired
    public ValidationEngine(List<Validator> validators) {
        if (validators != null) {
            validators.forEach(validator -> {
                String type = validator.getType();
                if (validatorMap.containsKey(type)) {
                    log.warn("Duplicate validator type: {}", type);
                }
                validatorMap.put(type, validator);
                // 初始化验证器
                validator.init(null);
                log.info("Registered validator: {}", type);
            });
        }
    }

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        log.info("ValidationEngine initialized with {} validators", validatorMap.size());
    }

    /**
     * 验证表单数据
     * @param rulesJson 验证规则JSON字符串
     * @param formDataJson 表单数据JSON字符串
     * @return 验证结果
     */
    public ValidationResult validate(String rulesJson, String formDataJson) {
        List<ValidationRule> rules = JSON.parseArray(rulesJson, ValidationRule.class);
        Map<String, Object> formData = JSON.parseObject(formDataJson, Map.class);
        return validate(rules, formData);
    }

    /**
     * 验证表单数据
     * @param rules 验证规则列表
     * @param formData 表单数据
     * @return 验证结果
     */
    public ValidationResult validate(List<ValidationRule> rules, Map<String, Object> formData) {
        if (rules == null || rules.isEmpty()) {
            return ValidationResult.success();
        }

        // 按验证顺序排序
        List<ValidationRule> sortedRules = rules.stream()
                .filter(rule -> rule.getEnabled() == null || rule.getEnabled())
                .sorted(Comparator.comparing(ValidationRule::getOrder, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());

        ValidationResult result = ValidationResult.success();

        for (ValidationRule rule : sortedRules) {
            // 检查验证条件
            if (!checkCondition(rule.getCondition(), formData)) {
                log.debug("Validation rule skipped for field {} due to condition not met", rule.getFieldName());
                continue;
            }

            // 执行验证
            ValidationResult ruleResult = validateRule(rule, formData);
            if (!ruleResult.isValid()) {
                result.addErrors(ruleResult.getErrors());
            }
        }

        return result;
    }

    /**
     * 验证单个规则
     * @param rule 验证规则
     * @param formData 表单数据
     * @return 验证结果
     */
    private ValidationResult validateRule(ValidationRule rule, Map<String, Object> formData) {
        String fieldName = rule.getFieldName();
        String validatorType = rule.getValidatorType();
        Map<String, Object> params = rule.getParams();
        Object fieldValue = formData.get(fieldName);

        // 获取验证器
        Validator validator = validatorMap.get(validatorType);
        if (validator == null) {
            log.error("Validator not found for type: {}", validatorType);
            String errorMessage = String.format("Unknown validator type: %s", validatorType);
            ValidationError error = ValidationError.create(fieldName, "VALIDATOR_NOT_FOUND", errorMessage, validatorType);
            return ValidationResult.failure(error);
        }

        // 执行验证
        ValidationResult result = validator.validate(fieldName, fieldValue, params, formData);

        // 如果有自定义错误信息，替换默认错误信息
        if (!result.isValid() && result.getErrors() != null && !result.getErrors().isEmpty()) {
            for (ValidationError error : result.getErrors()) {
                // 优先使用自定义错误信息
                if (rule.getErrorMessage() != null) {
                    error.setErrorMessage(rule.getErrorMessage());
                }
                // 优先使用自定义错误信息国际化key
                if (rule.getErrorMessageKey() != null) {
                    error.setErrorMessageKey(rule.getErrorMessageKey());
                }
                // 国际化错误信息
                if (error.getErrorMessageKey() != null) {
                    try {
                        String localizedMessage = MessageService.getMessage(
                                error.getErrorMessageKey(),
                                Locale.getDefault(),
                                error.getErrorMessageParams() != null ? error.getErrorMessageParams().values().toArray() : null
                        );
                        error.setErrorMessage(localizedMessage);
                    } catch (Exception e) {
                        log.warn("Failed to get localized message for key: {}", error.getErrorMessageKey(), e);
                    }
                }
            }
        }

        // 验证子规则
        if (rule.getChildren() != null && !rule.getChildren().isEmpty()) {
            ValidationResult childrenResult = validate(rule.getChildren(), formData);
            if (!childrenResult.isValid()) {
                result.addErrors(childrenResult.getErrors());
            }
        }

        return result;
    }

    /**
     * 检查条件是否满足
     * @param condition 条件
     * @param formData 表单数据
     * @return 条件是否满足
     */
    private boolean checkCondition(Condition condition, Map<String, Object> formData) {
        if (condition == null || condition.getConditions() == null || condition.getConditions().isEmpty()) {
            return true;
        }

        List<Condition.ConditionItem> conditions = condition.getConditions();
        Condition.LogicalOperator logicalOperator = condition.getLogicalOperator() != null ? condition.getLogicalOperator() : Condition.LogicalOperator.AND;

        for (Condition.ConditionItem item : conditions) {
            boolean itemResult = checkConditionItem(item, formData);

            if (logicalOperator == Condition.LogicalOperator.AND && !itemResult) {
                return false;
            }

            if (logicalOperator == Condition.LogicalOperator.OR && itemResult) {
                return true;
            }
        }

        // 如果是AND逻辑，所有条件都满足才返回true；如果是OR逻辑，所有条件都不满足才返回false
        return logicalOperator == Condition.LogicalOperator.AND;
    }

    /**
     * 检查单个条件项是否满足
     * @param item 条件项
     * @param formData 表单数据
     * @return 条件项是否满足
     */
    private boolean checkConditionItem(Condition.ConditionItem item, Map<String, Object> formData) {
        if (item == null || item.getFieldName() == null || item.getOperator() == null) {
            return false;
        }

        String fieldName = item.getFieldName();
        Object fieldValue = formData.get(fieldName);
        Object compareValue = item.getValue();
        Condition.ConditionItem.ComparisonOperator operator = item.getOperator();

        log.debug("Checking condition: {} {} {}", fieldValue, operator, compareValue);

        // 处理空值情况
        if (fieldValue == null && compareValue == null) {
            return operator == Condition.ConditionItem.ComparisonOperator.EQ;
        }
        if (fieldValue == null) {
            return operator == Condition.ConditionItem.ComparisonOperator.NE;
        }
        if (compareValue == null) {
            return operator == Condition.ConditionItem.ComparisonOperator.EQ;
        }

        // 转换为相同类型进行比较
        if (fieldValue.getClass() != compareValue.getClass()) {
            try {
                compareValue = convertToType(compareValue, fieldValue.getClass());
            } catch (Exception e) {
                log.warn("Failed to convert compare value {} to type {}", compareValue, fieldValue.getClass(), e);
                return false;
            }
        }

        // 执行比较
        switch (operator) {
            case EQ:
                return fieldValue.equals(compareValue);
            case NE:
                return !fieldValue.equals(compareValue);
            case GT:
                return compareValues(fieldValue, compareValue) > 0;
            case LT:
                return compareValues(fieldValue, compareValue) < 0;
            case GTE:
                return compareValues(fieldValue, compareValue) >= 0;
            case LTE:
                return compareValues(fieldValue, compareValue) <= 0;
            case CONTAINS:
                return contains(fieldValue, compareValue);
            case STARTS_WITH:
                return startsWith(fieldValue, compareValue);
            case ENDS_WITH:
                return endsWith(fieldValue, compareValue);
            case IN:
                return in(fieldValue, compareValue);
            case NOT_IN:
                return !in(fieldValue, compareValue);
            default:
                log.warn("Unknown comparison operator: {}", operator);
                return false;
        }
    }

    /**
     * 转换值到指定类型
     * @param value 要转换的值
     * @param targetType 目标类型
     * @return 转换后的值
     * @throws Exception 转换失败时抛出异常
     */
    private Object convertToType(Object value, Class<?> targetType) throws Exception {
        if (targetType == String.class) {
            return value.toString();
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value.toString());
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value.toString());
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value.toString());
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value.toString());
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value.toString());
        } else if (targetType == Date.class) {
            return new Date(Long.parseLong(value.toString()));
        } else {
            // 尝试使用JSON转换
            return JSON.parseObject(JSON.toJSONString(value), targetType);
        }
    }

    /**
     * 比较两个值的大小
     * @param value1 值1
     * @param value2 值2
     * @return 比较结果：1表示value1大于value2，-1表示value1小于value2，0表示相等
     */
    private int compareValues(Object value1, Object value2) {
        if (value1 instanceof Comparable && value2 instanceof Comparable) {
            Comparable comparable1 = (Comparable) value1;
            Comparable comparable2 = (Comparable) value2;
            return comparable1.compareTo(comparable2);
        }
        return 0;
    }

    /**
     * 检查值是否包含指定元素
     * @param value 值
     * @param element 元素
     * @return 是否包含
     */
    private boolean contains(Object value, Object element) {
        if (value instanceof String) {
            return ((String) value).contains(element.toString());
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).contains(element);
        } else if (value instanceof Map) {
            return ((Map<?, ?>) value).containsValue(element);
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            return Arrays.asList(array).contains(element);
        }
        return false;
    }

    /**
     * 检查字符串是否以指定前缀开头
     * @param value 值
     * @param prefix 前缀
     * @return 是否以指定前缀开头
     */
    private boolean startsWith(Object value, Object prefix) {
        if (value instanceof String) {
            return ((String) value).startsWith(prefix.toString());
        }
        return false;
    }

    /**
     * 检查字符串是否以指定后缀结尾
     * @param value 值
     * @param suffix 后缀
     * @return 是否以指定后缀结尾
     */
    private boolean endsWith(Object value, Object suffix) {
        if (value instanceof String) {
            return ((String) value).endsWith(suffix.toString());
        }
        return false;
    }

    /**
     * 检查值是否在指定集合中
     * @param value 值
     * @param collection 集合
     * @return 是否在集合中
     */
    private boolean in(Object value, Object collection) {
        if (collection instanceof Collection) {
            return ((Collection<?>) collection).contains(value);
        } else if (collection instanceof Map) {
            return ((Map<?, ?>) collection).containsValue(value);
        } else if (collection.getClass().isArray()) {
            Object[] array = (Object[]) collection;
            return Arrays.asList(array).contains(value);
        } else if (collection instanceof String) {
            // 处理逗号分隔的字符串
            String[] parts = ((String) collection).split(",");
            return Arrays.asList(parts).contains(value.toString());
        }
        return false;
    }
}
