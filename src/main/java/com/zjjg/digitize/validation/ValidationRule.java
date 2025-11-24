package com.zjjg.digitize.validation;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 验证规则配置类
 * 支持条件判断、跨字段验证和国际化错误信息
 */
@Data
public class ValidationRule {

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 验证器类型（如：required、email、minLength等）
     */
    private String validatorType;

    /**
     * 验证器参数（如：minLength的参数是最小长度值）
     */
    private Map<String, Object> params;

    /**
     * 错误信息模板
     */
    private String errorMessage;

    /**
     * 错误信息国际化key
     */
    private String errorMessageKey;

    /**
     * 验证条件
     */
    private Condition condition;

    /**
     * 子验证规则（用于复杂验证场景）
     */
    private List<ValidationRule> children;

    /**
     * 验证顺序
     */
    private Integer order;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
