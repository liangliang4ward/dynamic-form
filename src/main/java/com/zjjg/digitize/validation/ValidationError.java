package com.zjjg.digitize.validation;

import lombok.Data;

/**
 * 验证错误实体类
 * 包含字段名和错误信息
 */
@Data
public class ValidationError {
    
    /**
     * 字段名
     */
    private String field;
    
    /**
     * 错误信息
     */
    private String message;
    
    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
