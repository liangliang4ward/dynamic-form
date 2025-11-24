package com.zjjg.digitize.validation;

import com.zjjg.digitize.validation.validators.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 验证引擎测试类
 */
public class ValidationEngineTest {

    private ValidationEngine validationEngine;

    @Before
    public void setUp() {
        // 手动创建验证器列表
        List<Validator> validators = new ArrayList<>();
        validators.add(new RequiredValidator());
        validators.add(new EmailValidator());
        validators.add(new MinLengthValidator());
        validators.add(new MaxLengthValidator());
        validators.add(new RangeValidator());
        validators.add(new RegexValidator());

        // 创建验证引擎
        validationEngine = new ValidationEngine(validators);
    }

    @Test
    public void testRequiredValidator() {
        // 创建验证规则
        List<ValidationRule> rules = new ArrayList<>();
        ValidationRule rule = new ValidationRule();
        rule.setFieldName("username");
        rule.setValidatorType("required");
        rules.add(rule);

        // 测试空值
        Map<String, Object> formData = new HashMap<>();
        formData.put("username", null);
        ValidationResult result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("username", result.getErrors().get(0).getFieldName());
        assertEquals("FIELD_REQUIRED", result.getErrors().get(0).getErrorCode());

        // 测试空字符串
        formData.put("username", "");
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());

        // 测试非空值
        formData.put("username", "test");
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }

    @Test
    public void testEmailValidator() {
        // 创建验证规则
        List<ValidationRule> rules = new ArrayList<>();
        ValidationRule rule = new ValidationRule();
        rule.setFieldName("email");
        rule.setValidatorType("email");
        rules.add(rule);

        // 测试空值
        Map<String, Object> formData = new HashMap<>();
        formData.put("email", null);
        ValidationResult result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());

        // 测试无效邮箱
        formData.put("email", "invalid-email");
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("EMAIL_INVALID_FORMAT", result.getErrors().get(0).getErrorCode());

        // 测试有效邮箱
        formData.put("email", "test@example.com");
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }

    @Test
    public void testMinLengthValidator() {
        // 创建验证规则
        List<ValidationRule> rules = new ArrayList<>();
        ValidationRule rule = new ValidationRule();
        rule.setFieldName("password");
        rule.setValidatorType("minLength");
        Map<String, Object> params = new HashMap<>();
        params.put("minLength", 6);
        rule.setParams(params);
        rules.add(rule);

        // 测试空值
        Map<String, Object> formData = new HashMap<>();
        formData.put("password", null);
        ValidationResult result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());

        // 测试长度不足
        formData.put("password", "12345");
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("MIN_LENGTH_NOT_MET", result.getErrors().get(0).getErrorCode());

        // 测试长度足够
        formData.put("password", "123456");
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }

    @Test
    public void testMaxLengthValidator() {
        // 创建验证规则
        List<ValidationRule> rules = new ArrayList<>();
        ValidationRule rule = new ValidationRule();
        rule.setFieldName("username");
        rule.setValidatorType("maxLength");
        Map<String, Object> params = new HashMap<>();
        params.put("maxLength", 10);
        rule.setParams(params);
        rules.add(rule);

        // 测试空值
        Map<String, Object> formData = new HashMap<>();
        formData.put("username", null);
        ValidationResult result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());

        // 测试长度超出
        formData.put("username", "12345678901");
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("MAX_LENGTH_EXCEEDED", result.getErrors().get(0).getErrorCode());

        // 测试长度不超出
        formData.put("username", "1234567890");
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }

    @Test
    public void testRangeValidator() {
        // 创建验证规则
        List<ValidationRule> rules = new ArrayList<>();
        ValidationRule rule = new ValidationRule();
        rule.setFieldName("age");
        rule.setValidatorType("range");
        Map<String, Object> params = new HashMap<>();
        params.put("min", 18);
        params.put("max", 60);
        rule.setParams(params);
        rules.add(rule);

        // 测试空值
        Map<String, Object> formData = new HashMap<>();
        formData.put("age", null);
        ValidationResult result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());

        // 测试小于最小值
        formData.put("age", 17);
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("RANGE_BELOW_MIN", result.getErrors().get(0).getErrorCode());

        // 测试大于最大值
        formData.put("age", 61);
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("RANGE_ABOVE_MAX", result.getErrors().get(0).getErrorCode());

        // 测试在范围内
        formData.put("age", 30);
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }

    @Test
    public void testRegexValidator() {
        // 创建验证规则
        List<ValidationRule> rules = new ArrayList<>();
        ValidationRule rule = new ValidationRule();
        rule.setFieldName("phone");
        rule.setValidatorType("regex");
        Map<String, Object> params = new HashMap<>();
        params.put("regex", "^1[3-9]\\d{9}$");
        rule.setParams(params);
        rules.add(rule);

        // 测试空值
        Map<String, Object> formData = new HashMap<>();
        formData.put("phone", null);
        ValidationResult result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());

        // 测试不匹配
        formData.put("phone", "1234567890");
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("REGEX_NOT_MATCHED", result.getErrors().get(0).getErrorCode());

        // 测试匹配
        formData.put("phone", "13800138000");
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }

    @Test
    public void testCondition() {
        // 创建验证规则：当gender为male时，age必须大于等于18
        List<ValidationRule> rules = new ArrayList<>();
        ValidationRule rule = new ValidationRule();
        rule.setFieldName("age");
        rule.setValidatorType("range");
        Map<String, Object> params = new HashMap<>();
        params.put("min", 18);
        rule.setParams(params);

        // 创建条件
        Condition condition = new Condition();
        condition.setLogicalOperator(Condition.LogicalOperator.AND);
        List<Condition.ConditionItem> conditions = new ArrayList<>();
        Condition.ConditionItem item = new Condition.ConditionItem();
        item.setFieldName("gender");
        item.setOperator(Condition.ConditionItem.ComparisonOperator.EQ);
        item.setValue("male");
        conditions.add(item);
        condition.setConditions(conditions);
        rule.setCondition(condition);

        rules.add(rule);

        // 测试条件不满足时不验证
        Map<String, Object> formData = new HashMap<>();
        formData.put("gender", "female");
        formData.put("age", 17);
        ValidationResult result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());

        // 测试条件满足时验证
        formData.put("gender", "male");
        result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());

        // 测试条件满足且验证通过
        formData.put("age", 18);
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }

    @Test
    public void testMultipleRules() {
        // 创建多个验证规则
        List<ValidationRule> rules = new ArrayList<>();

        // 用户名必填，长度在3-20之间
        ValidationRule usernameRule1 = new ValidationRule();
        usernameRule1.setFieldName("username");
        usernameRule1.setValidatorType("required");
        rules.add(usernameRule1);

        ValidationRule usernameRule2 = new ValidationRule();
        usernameRule2.setFieldName("username");
        usernameRule2.setValidatorType("minLength");
        Map<String, Object> minParams = new HashMap<>();
        minParams.put("minLength", 3);
        usernameRule2.setParams(minParams);
        rules.add(usernameRule2);

        ValidationRule usernameRule3 = new ValidationRule();
        usernameRule3.setFieldName("username");
        usernameRule3.setValidatorType("maxLength");
        Map<String, Object> maxParams = new HashMap<>();
        maxParams.put("maxLength", 20);
        usernameRule3.setParams(maxParams);
        rules.add(usernameRule3);

        // 邮箱必填且格式正确
        ValidationRule emailRule1 = new ValidationRule();
        emailRule1.setFieldName("email");
        emailRule1.setValidatorType("required");
        rules.add(emailRule1);

        ValidationRule emailRule2 = new ValidationRule();
        emailRule2.setFieldName("email");
        emailRule2.setValidatorType("email");
        rules.add(emailRule2);

        // 测试所有规则都不满足
        Map<String, Object> formData = new HashMap<>();
        formData.put("username", "ab");
        formData.put("email", "invalid-email");
        ValidationResult result = validationEngine.validate(rules, formData);
        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size()); // username长度不足，email格式错误

        // 测试所有规则都满足
        formData.put("username", "testuser");
        formData.put("email", "test@example.com");
        result = validationEngine.validate(rules, formData);
        assertTrue(result.isValid());
    }
}
