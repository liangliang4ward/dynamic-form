package com.zjjg.digitize.validation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    /**
     * 是否验证通过
     */
    private boolean isValid;

    /**
     * 错误信息列表
     */
    private List<ValidationError> errors;

    /**
     * 添加错误信息
     * @param error 错误信息
     */
    public void addError(ValidationError error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
        isValid = false;
    }

    /**
     * 添加错误信息列表
     * @param errors 错误信息列表
     */
    public void addErrors(List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return;
        }
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.addAll(errors);
        isValid = false;
    }

    /**
     * 创建验证通过的结果
     * @return 验证通过的结果
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    /**
     * 创建验证失败的结果
     * @param error 错误信息
     * @return 验证失败的结果
     */
    public static ValidationResult failure(ValidationError error) {
        ValidationResult result = new ValidationResult(false, new ArrayList<>());
        result.addError(error);
        return result;
    }

    /**
     * 创建验证失败的结果
     * @param errors 错误信息列表
     * @return 验证失败的结果
     */
    public static ValidationResult failure(List<ValidationError> errors) {
        return new ValidationResult(false, errors);
    }
}
