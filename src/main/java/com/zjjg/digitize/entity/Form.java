package com.zjjg.digitize.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Form entity representing the form metadata
 */
@Data
@TableName("sys_form")
public class Form {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String status; // draft, published
    private String tableName;
    private Long createdBy;
    private LocalDateTime createTime;
    private Long updatedBy;
    private LocalDateTime updateTime;
}