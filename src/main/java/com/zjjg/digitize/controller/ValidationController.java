package com.zjjg.digitize.controller;

import com.zjjg.digitize.validation.ValidationEngine;
import com.zjjg.digitize.validation.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 验证控制器
 * 提供REST API接口来接收验证请求并返回验证结果
 */
@Slf4j
@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    @Autowired
    private ValidationEngine validationEngine;

    /**
     * 验证表单数据
     * @param request 请求参数，包含验证规则和表单数据
     * @return 验证结果
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validate(@RequestBody ValidationRequest request) {
        try {
            log.debug("Received validation request: rules={}, formData={}", request.getRules(), request.getFormData());

            ValidationResult result = validationEngine.validate(request.getRules(), request.getFormData());

            log.debug("Validation result: isValid={}, errors={}", result.isValid(), result.getErrors());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Validation failed", e);
            ValidationResult result = ValidationResult.failure(
                    com.zjjg.digitize.validation.ValidationError.create(
                            null,
                            "VALIDATION_FAILED",
                            "Validation failed due to an unexpected error",
                            "validation.failed",
                            null,
                            null
                    )
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 验证请求参数类
     */
    public static class ValidationRequest {
        private String rules;
        private String formData;

        public String getRules() {
            return rules;
        }

        public void setRules(String rules) {
            this.rules = rules;
        }

        public String getFormData() {
            return formData;
        }

        public void setFormData(String formData) {
            this.formData = formData;
        }
    }
}
