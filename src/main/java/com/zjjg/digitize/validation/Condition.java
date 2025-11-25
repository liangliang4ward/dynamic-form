package com.zjjg.digitize.validation;

import lombok.Data;
import java.util.List;

/**
 * 条件判断类
 * 支持单条件和多条件组合（AND/OR）
 */
@Data
public class Condition {
    
    /**
     * 条件类型：single（单条件）、and（多条件AND）、or（多条件OR）
     */
    private String type;
    
    /**
     * 单条件配置（当type为single时使用）
     */
    private SingleCondition single;
    
    /**
     * 多条件配置（当type为and或or时使用）
     */
    private List<Condition> conditions;
    
    /**
     * 单条件实体类
     */
    @Data
    public static class SingleCondition {
        
        /**
         * 字段名
         */
        private String field;
        
        /**
         * 操作符（如：eq, ne, gt, lt, contains等）
         */
        private String operator;
        
        /**
         * 值
         */
        private Object value;
    }
}
