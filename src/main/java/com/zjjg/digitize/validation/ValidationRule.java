package com.zjjg.digitize.validation;

import lombok.Data;
import java.util.Map;

/**
 * 验证规则实体类
 * 包含验证类型、参数、错误信息和条件判断
 */
@Data
public class ValidationRule {
    
    /**
     * 验证类型（如：required, email, maxLength等）
     */
    private String type;
    
    /**
     * 验证参数（如：maxLength的长度值，regex的正则表达式等）
     */
    private Map<String, Object> params;
    
    /**
     * 错误信息
     */
    private String message;
    
    /**
     * 验证条件（可选）
     */
    private Condition condition;
}
