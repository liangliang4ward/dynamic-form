package com.zjjg.digitize.validation.validators;

import com.zjjg.digitize.validation.ValidationResult;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 邮箱验证器
 * 验证字段值是否为有效的邮箱格式
 */
public class EmailValidator extends BaseValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public EmailValidator() {
        super("email");
    }

    @Override
    public ValidationResult validate(String fieldName, Object fieldValue, Map<String, Object> params, Map<String, Object> formData) {
        // 如果值为空，不进行验证（必填验证应该由RequiredValidator单独处理）
        if (isEmpty(fieldValue)) {
            return createSuccessResult();
        }

        if (!(fieldValue instanceof String)) {
            return createFailureResult(
                    fieldName,
                    "EMAIL_INVALID_TYPE",
                    String.format("Field '%s' must be a string", fieldName),
                    "validation.email.invalid.type",
                    params
            );
        }

        String email = (String) fieldValue;
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return createFailureResult(
                    fieldName,
                    "EMAIL_INVALID_FORMAT",
                    String.format("Field '%s' is not a valid email address", fieldName),
                    "validation.email.invalid.format",
                    params
            );
        }

        return createSuccessResult();
    }
}
