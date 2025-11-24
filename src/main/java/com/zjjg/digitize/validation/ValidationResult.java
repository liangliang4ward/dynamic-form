package com.zjjg.digitize.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果类，封装表单验证的结果
 */
public class ValidationResult {
    private boolean isValid;
    private List<ValidationError> errors;

    public ValidationResult() {
        this.isValid = true;
        this.errors = new ArrayList<>();
    }

    public void addError(ValidationError error) {
        this.isValid = false;
        this.errors.add(error);
    }

    public boolean isValid() { return isValid; }
    public List<ValidationError> getErrors() { return errors; }
}