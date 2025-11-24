package com.zjjg.digitize.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjjg.digitize.validation.ValidationEngine;
import com.zjjg.digitize.validation.ValidationResult;
import com.zjjg.digitize.validation.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import java.util.Locale;

/**
 * 验证控制器，演示如何使用验证引擎
 */
@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    @Autowired
    private ValidationEngine validationEngine;

    /**
     * 验证表单数据
     * @param request 请求体，包含验证规则和表单数据
     * @return 验证结果
     */
    @PostMapping("/validate")
    public ValidationResult validateForm(@RequestBody JSONObject request) {
        JSONArray rulesJson = request.getJSONArray("rules");
        JSONObject formData = request.getJSONObject("formData");
        String localeStr = request.getString("locale");

        // 解析验证规则
        List<ValidationRule> rules = validationEngine.parseRules(rulesJson);

        // 确定国际化区域
        Locale locale = Locale.CHINA;
        if ("en_US".equals(localeStr)) {
            locale = Locale.US;
        }

        // 执行验证
        return validationEngine.validate(formData, rules, locale);
    }
}