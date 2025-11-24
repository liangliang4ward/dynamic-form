package com.zjjg.digitize.validation.validators;

import com.zjjg.digitize.validation.ValidationResult;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 正则表达式验证器
 * 验证字段值是否匹配指定的正则表达式
 */
public class RegexValidator extends BaseValidator {

    public RegexValidator() {
        super("regex");
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
                    "REGEX_INVALID_TYPE",
                    String.format("Field '%s' must be a string", fieldName),
                    "validation.regex.invalid.type",
                    params
            );
        }

        // 获取正则表达式参数
        String regex = getRegexParam(params);
        if (regex == null) {
            return createFailureResult(
                    fieldName,
                    "REGEX_PARAM_REQUIRED",
                    "Regex parameter is required",
                    "validation.regex.param.required",
                    params
            );
        }

        // 编译正则表达式
        Pattern pattern = compilePattern(regex);
        if (pattern == null) {
            return createFailureResult(
                    fieldName,
                    "REGEX_INVALID_PATTERN",
                    String.format("Invalid regex pattern: %s", regex),
                    "validation.regex.invalid.pattern",
                    params
            );
        }

        String value = (String) fieldValue;
        if (!pattern.matcher(value).matches()) {
            return createFailureResult(
                    fieldName,
                    "REGEX_NOT_MATCHED",
                    String.format("Field '%s' does not match the required pattern", fieldName),
                    "validation.regex.not.matched",
                    params
            );
        }

        return createSuccessResult();
    }

    /**
     * 获取正则表达式参数
     * @param params 验证器参数
     * @return 正则表达式
     */
    private String getRegexParam(Map<String, Object> params) {
        if (params == null) {
            return null;
        }

        Object regexObj = params.get("regex");
        if (regexObj == null) {
            return null;
        }

        return regexObj.toString();
    }

    /**
     * 编译正则表达式
     * @param regex 正则表达式字符串
     * @return 编译后的Pattern对象
     */
    private Pattern compilePattern(String regex) {
        try {
            return Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            return null;
        }
    }
}
