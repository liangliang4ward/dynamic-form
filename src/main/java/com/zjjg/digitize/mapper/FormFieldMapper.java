package com.zjjg.digitize.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjjg.digitize.entity.FormField;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * Mapper interface for FormField entity
 */
@Mapper
public interface FormFieldMapper extends BaseMapper<FormField> {
    /**
     * Get all fields for a form
     * @param formId form id
     * @return list of form fields
     */
    List<FormField> selectByFormId(Long formId);
}