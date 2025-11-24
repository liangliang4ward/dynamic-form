package com.zjjg.digitize.validation;

import lombok.Data;

import java.util.List;

/**
 * 条件判断类
 * 支持逻辑运算符（AND/OR）和比较运算符（==, !=, >, <, >=, <=, contains等）
 */
@Data
public class Condition {

    /**
     * 逻辑运算符：AND/OR
     */
    private LogicalOperator logicalOperator;

    /**
     * 条件列表
     */
    private List<ConditionItem> conditions;

    /**
     * 逻辑运算符枚举
     */
    public enum LogicalOperator {
        AND, OR
    }

    /**
     * 条件项
     */
    @Data
    public static class ConditionItem {

        /**
         * 字段名称
         */
        private String fieldName;

        /**
         * 比较运算符
         */
        private ComparisonOperator operator;

        /**
         * 比较值
         */
        private Object value;

        /**
         * 比较运算符枚举
         */
        public enum ComparisonOperator {
            EQ, NE, GT, LT, GTE, LTE, CONTAINS, STARTS_WITH, ENDS_WITH, IN, NOT_IN
        }
    }
}
