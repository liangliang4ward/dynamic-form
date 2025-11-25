package com.zjjg.digitize.validation;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果类
 * 包含验证是否通过和错误信息列表
 */
@Data
public class ValidationResult {
    
    /**
     * 验证是否通过
     */
    private boolean passed;
    
    /**
     * 错误信息列表
     */
    private List<ValidationError> errors;
    
    public ValidationResult() {
        this.passed = true;
        this.errors = new ArrayList<>();
    }
    
    /**
     * 添加错误信息
     * @param error 错误信息
     */
    public void addError(ValidationError error) {
        this.passed = false;
        this.errors.add(error);
    }
    
    /**
     * 添加错误信息
     * @param field 字段名
     * @param message 错误信息
     */
    public void addError(String field, String message) {
        this.addError(new ValidationError(field, message));
    }
}
