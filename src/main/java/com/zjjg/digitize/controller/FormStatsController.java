package com.zjjg.digitize.controller;

import com.zjjg.digitize.common.ApiResponse;
import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.service.FormService;
import com.zjjg.digitize.service.FormStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for form data statistics and analysis
 */
@RestController
@RequestMapping("/api/form-stats")
public class FormStatsController {

    @Autowired
    private FormStatsService formStatsService;

    @Autowired
    private FormService formService;

    /**
     * Get basic statistics for a form
     * @param formId form id
     * @return ApiResponse with statistics
     */
    @GetMapping("/{formId}")
    public ApiResponse<Map<String, Object>> getFormStats(@PathVariable Long formId) {
        Form form = formService.getById(formId);
        if (form == null) {
            return ApiResponse.error(404, "Form not found with id: " + formId);
        }
        if (!"published".equals(form.getStatus())) {
            return ApiResponse.error(400, "Form is not published, cannot get statistics");
        }
        Map<String, Object> stats = formStatsService.getFormStats(form.getTableName());
        return ApiResponse.success(stats);
    }

    /**
     * Get field statistics for a form
     * @param formId form id
     * @param fieldName field name
     * @return ApiResponse with field statistics
     */
    @GetMapping("/{formId}/field/{fieldName}")
    public ApiResponse<Map<String, Object>> getFieldStats(@PathVariable Long formId, @PathVariable String fieldName) {
        Form form = formService.getById(formId);
        if (form == null) {
            return ApiResponse.error(404, "Form not found with id: " + formId);
        }
        if (!"published".equals(form.getStatus())) {
            return ApiResponse.error(400, "Form is not published, cannot get statistics");
        }
        Map<String, Object> stats = formStatsService.getFieldStats(form.getTableName(), fieldName);
        return ApiResponse.success(stats);
    }
}