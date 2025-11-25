package com.zjjg.digitize.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjjg.digitize.entity.Form;
import java.sql.SQLException;

/**
 * Service interface for Form entity
 */
public interface FormService extends IService<Form> {
    /**
     * Publish a form and generate the corresponding database table
     * @param formId form id
     * @return boolean indicating success
     * @throws SQLException if there's an error creating the database table
     */
    boolean publishForm(Long formId) throws SQLException;
}