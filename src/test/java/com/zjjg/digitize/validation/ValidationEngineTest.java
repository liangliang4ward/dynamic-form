package com.zjjg.digitize.validation;

import com.zjjg.digitize.DynamicFormApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 验证引擎测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DynamicFormApplication.class)
public class ValidationEngineTest {
    
    @Autowired
    private ValidationEngine validationEngine;
    
    @Test
    public void testValidation() {
        // 定义验证规则JSON
        String jsonConfig = "{\"username\": [{\"type\": \"required\", \"message\": \"用户名不能为空\"},{\"type\": \"minLength\", \"params\": {\"length\": 3}, \"message\": \"用户名长度不能少于3个字符\"},{\"type\": \"maxLength\", \"params\": {\"length\": 20}, \"message\": \"用户名长度不能超过20个字符\"}],\"email\": [{\"type\": \"required\", \"message\": \"邮箱不能为空\"},{\"type\": \"email\", \"message\": \"邮箱格式不正确\"}],\"password\": [{\"type\": \"required\", \"message\": \"密码不能为空\"},{\"type\": \"minLength\", \"params\": {\"length\": 6}, \"message\": \"密码长度不能少于6个字符\"}],\"confirmPassword\": [{\"type\": \"required\", \"message\": \"确认密码不能为空\"},{\"type\": \"regex\", \"params\": {\"pattern\": \"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)[a-zA-Z\\\\d]{6,}$\"}, \"message\": \"密码必须包含大小写字母和数字\"}]}";
        
        // 解析验证规则
        Map<String, List<ValidationRule>> rules = validationEngine.parseRules(jsonConfig);
        
        // 准备表单数据
        Map<String, Object> formData = new HashMap<>();
        formData.put("username", "admin");
        formData.put("email", "admin@example.com");
        formData.put("password", "Admin123");
        formData.put("confirmPassword", "Admin123");
        
        // 执行验证
        ValidationResult result = validationEngine.validate(rules, formData);
        
        // 验证结果应该通过
        assertTrue(result.isPassed());
        assertTrue(result.getErrors().isEmpty());
    }
    
    @Test
    public void testValidationWithErrors() {
        // 定义验证规则JSON
        String jsonConfig = "{\"username\": [{\"type\": \"required\", \"message\": \"用户名不能为空\"},{\"type\": \"minLength\", \"params\": {\"length\": 3}, \"message\": \"用户名长度不能少于3个字符\"}],\"email\": [{\"type\": \"required\", \"message\": \"邮箱不能为空\"},{\"type\": \"email\", \"message\": \"邮箱格式不正确\"}]}";
        
        // 解析验证规则
        Map<String, List<ValidationRule>> rules = validationEngine.parseRules(jsonConfig);
        
        // 准备表单数据（包含错误）
        Map<String, Object> formData = new HashMap<>();
        formData.put("username", "ab"); // 长度不足3个字符
        formData.put("email", "invalid-email"); // 邮箱格式不正确
        
        // 执行验证
        ValidationResult result = validationEngine.validate(rules, formData);
        
        // 验证结果应该不通过
        assertFalse(result.isPassed());
        assertEquals(2, result.getErrors().size());
        
        // 验证错误信息
        Map<String, String> errorMap = new HashMap<>();
        for (ValidationError error : result.getErrors()) {
            errorMap.put(error.getField(), error.getMessage());
        }
        
        assertEquals("用户名长度不能少于3个字符", errorMap.get("username"));
        assertEquals("邮箱格式不正确", errorMap.get("email"));
    }
    
    @Test
    public void testValidationWithCondition() {
        // 定义验证规则JSON（包含条件判断）
        String jsonConfig = "{\"age\": [{\"type\": \"required\", \"message\": \"年龄不能为空\"}],\"driverLicense\": [{\"type\": \"required\", \"message\": \"驾驶证号码不能为空\",\"condition\": {\"type\": \"single\",\"single\": {\"field\": \"age\",\"operator\": \"ge\",\"value\": 18}}}]}";
        
        // 解析验证规则
        Map<String, List<ValidationRule>> rules = validationEngine.parseRules(jsonConfig);
        
        // 准备表单数据（年龄大于18岁，需要驾驶证）
        Map<String, Object> formData1 = new HashMap<>();
        formData1.put("age", 20);
        formData1.put("driverLicense", ""); // 驾驶证为空
        
        // 执行验证
        ValidationResult result1 = validationEngine.validate(rules, formData1);
        
        // 验证结果应该不通过
        assertFalse(result1.isPassed());
        assertEquals(1, result1.getErrors().size());
        assertEquals("驾驶证号码不能为空", result1.getErrors().get(0).getMessage());
        
        // 准备表单数据（年龄小于18岁，不需要驾驶证）
        Map<String, Object> formData2 = new HashMap<>();
        formData2.put("age", 16);
        formData2.put("driverLicense", ""); // 驾驶证为空
        
        // 执行验证
        ValidationResult result2 = validationEngine.validate(rules, formData2);
        
        // 验证结果应该通过
        assertTrue(result2.isPassed());
        assertTrue(result2.getErrors().isEmpty());
    }
}
