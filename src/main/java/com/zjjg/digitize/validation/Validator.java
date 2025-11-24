package com.zjjg.digitize.validation;

import java.util.Map;

/**
 * 验证器接口
 * 所有具体验证器都需要实现此接口
 */
public interface Validator {

    /**
     * 获取验证器类型
     * @return 验证器类型
     */
    String getType();

    /**
     * 验证字段值
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @param params 验证器参数
     * @param formData 整个表单数据（用于跨字段验证）
     * @return 验证结果
     */
    ValidationResult validate(String fieldName, Object fieldValue, Map<String, Object> params, Map<String, Object> formData);

    /**
     * 初始化验证器
     * @param params 验证器参数
     */
    default void init(Map<String, Object> params) {
        // 默认实现为空，子类可以重写
    }
}
