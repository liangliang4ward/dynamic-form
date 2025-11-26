package com.zjjg.digitize.service;

import java.util.Map;

/**
 * Service interface for form data statistics and analysis
 */
public interface FormStatsService {
    /**
     * Get basic statistics for a form
     * @param tableName database table name
     * @return statistics map
     */
    Map<String, Object> getFormStats(String tableName);

    /**
     * Get field statistics for a form
     * @param tableName database table name
     * @param fieldName field name
     * @return field statistics map
     */
    Map<String, Object> getFieldStats(String tableName, String fieldName);
}