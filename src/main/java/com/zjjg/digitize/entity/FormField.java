package com.zjjg.digitize.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * FormField entity representing the metadata for each field in a form
 */
@Data
@TableName("sys_form_field")
public class FormField {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long formId;
    private String fieldName;
    private String fieldType;
    private String fieldLabel;
    private Integer fieldLength;
    private boolean primaryKey;
    private boolean notNull;
    private String defaultValue;
    private Integer sortOrder;
}