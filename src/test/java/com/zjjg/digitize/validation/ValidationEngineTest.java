package com.zjjg.digitize.validation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjjg.digitize.validation.validators.RequiredValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * 验证引擎测试类
 */
public class ValidationEngineTest {

    private ValidationEngine validationEngine;

    @Before
    public void setUp() {
        validationEngine = new ValidationEngine();
        // 注册必填验证器
        validationEngine.registerValidator(new RequiredValidator());
    }

    @Test
    public void testParseRules() {
        // 创建JSON格式的验证规则
        String jsonRulesStr = "[{\"fieldName\":\"username\",\"validatorType\":\"required\",\"errorMessageKey\":\"username.required\",\"condition\":{\"conditionType\":\"equals\",\"fieldName\":\"isRegister\",\"compareValue\":true}}]";

        JSONArray jsonRules = JSONArray.parseArray(jsonRulesStr);
        List<ValidationRule> rules = validationEngine.parseRules(jsonRules);

        assertEquals(1, rules.size());
        ValidationRule rule = rules.get(0);
        assertEquals("username", rule.getFieldName());
        assertEquals("required", rule.getValidatorType());
        assertEquals("username.required", rule.getErrorMessageKey());
        assertNotNull(rule.getCondition());
        assertEquals("equals", rule.getCondition().getConditionType());
        assertEquals("isRegister", rule.getCondition().getFieldName());
        assertEquals(true, rule.getCondition().getCompareValue());
    }

    @Test
    public void testValidateRequiredField() {
        // 创建验证规则
        String jsonRulesStr = "[{\"fieldName\":\"username\",\"validatorType\":\"required\",\"errorMessageKey\":\"username.required\"}]";

        JSONArray jsonRules = JSONArray.parseArray(jsonRulesStr);
        List<ValidationRule> rules = validationEngine.parseRules(jsonRules);

        // 创建表单数据（缺少必填字段）
        JSONObject formData = new JSONObject();
        formData.put("email", "test@example.com");

        // 执行验证
        ValidationResult result = validationEngine.validate(formData, rules, Locale.CHINA);

        // 验证结果应该包含错误
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("username", result.getErrors().get(0).getFieldName());
        assertEquals("此字段为必填项", result.getErrors().get(0).getErrorMessage());
    }

    @Test
    public void testValidateWithCondition() {
        // 创建验证规则，只有当isRegister为true时才验证username必填
        String jsonRulesStr = "[{\"fieldName\":\"username\",\"validatorType\":\"required\",\"errorMessageKey\":\"username.required\",\"condition\":{\"conditionType\":\"equals\",\"fieldName\":\"isRegister\",\"compareValue\":true}}]";

        System.out.println(jsonRulesStr);
        JSONArray jsonRules = JSONArray.parseArray(jsonRulesStr);
        List<ValidationRule> rules = validationEngine.parseRules(jsonRules);

        // 创建表单数据，isRegister为false，所以不应该验证username必填
        JSONObject formData = new JSONObject();
        formData.put("isRegister", false);

        // 执行验证
        ValidationResult result = validationEngine.validate(formData, rules, Locale.CHINA);

        // 验证结果应该通过
        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
    }
}
