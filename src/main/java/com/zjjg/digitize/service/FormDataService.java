package com.zjjg.digitize.service;

import java.util.List;
import java.util.Map;

/**
 * Service interface for form data management
 */
public interface FormDataService {
    /**
     * Save form data to the database
     * @param tableName database table name
     * @param formData form data
     * @return saved form data
     */
    Map<String, Object> saveFormData(String tableName, Map<String, Object> formData);

    /**
     * Update form data in the database
     * @param tableName database table name
     * @param id record id
     * @param formData form data
     * @return updated form data
     */
    Map<String, Object> updateFormData(String tableName, Object id, Map<String, Object> formData);

    /**
     * Get form data by id from the database
     * @param tableName database table name
     * @param id record id
     * @return form data
     */
    Map<String, Object> getFormDataById(String tableName, Object id);

    /**
     * Delete form data by id from the database
     * @param tableName database table name
     * @param id record id
     * @return boolean indicating success
     */
    boolean deleteFormDataById(String tableName, Object id);

    /**
     * List form data from the database with pagination
     * @param tableName database table name
     * @param page page number
     * @param size page size
     * @return list of form data
     */
    List<Map<String, Object>> listFormData(String tableName, int page, int size);
}