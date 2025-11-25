package com.zjjg.digitize.validation;

import java.util.Map;

/**
 * 验证器接口
 * 所有具体验证器都需要实现此接口
 */
public interface Validator {
    
    /**
     * 获取验证类型
     * @return 验证类型
     */
    String getType();
    
    /**
     * 验证字段值
     * @param fieldName 字段名
     * @param value 字段值
     * @param params 验证参数
     * @param formData 表单数据（用于跨字段验证）
     * @return 验证结果，null表示验证通过，否则返回错误信息
     */
    String validate(String fieldName, Object value, Map<String, Object> params, Map<String, Object> formData);
}
