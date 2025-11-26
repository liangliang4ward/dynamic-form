package com.zjjg.digitize.controller;

import com.zjjg.digitize.common.ApiResponse;
import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.service.FormDataService;
import com.zjjg.digitize.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing form data
 */
@RestController
@RequestMapping("/api/form-data")
public class FormDataController {

    @Autowired
    private FormDataService formDataService;

    @Autowired
    private FormService formService;

    /**
     * Save form data
     * @param formId form id
     * @param formData form data
     * @return ApiResponse with saved data
     */
    @PostMapping("/{formId}")
    public ApiResponse<Map<String, Object>> saveFormData(@PathVariable Long formId, @RequestBody Map<String, Object> formData) {
        Form form = formService.getById(formId);
        if (form == null) {
            return ApiResponse.error(404, "Form not found with id: " + formId);
        }
        if (!"published".equals(form.getStatus())) {
            return ApiResponse.error(400, "Form is not published, cannot save data");
        }
        Map<String, Object> savedData = formDataService.saveFormData(form.getTableName(), formData);
        return ApiResponse.success(savedData);
    }

    /**
     * Update form data
     * @param formId form id
     * @param id record id
     * @param formData form data
     * @return ApiResponse with updated data
     */
    @PutMapping("/{formId}/{id}")
    public ApiResponse<Map<String, Object>> updateFormData(@PathVariable Long formId, @PathVariable Object id, @RequestBody Map<String, Object> formData) {
        Form form = formService.getById(formId);
        if (form == null) {
            return ApiResponse.error(404, "Form not found with id: " + formId);
        }
        if (!"published".equals(form.getStatus())) {
            return ApiResponse.error(400, "Form is not published, cannot update data");
        }
        Map<String, Object> updatedData = formDataService.updateFormData(form.getTableName(), id, formData);
        return ApiResponse.success(updatedData);
    }

    /**
     * Get form data by id
     * @param formId form id
     * @param id record id
     * @return ApiResponse with form data
     */
    @GetMapping("/{formId}/{id}")
    public ApiResponse<Map<String, Object>> getFormDataById(@PathVariable Long formId, @PathVariable Object id) {
        Form form = formService.getById(formId);
        if (form == null) {
            return ApiResponse.error(404, "Form not found with id: " + formId);
        }
        Map<String, Object> formData = formDataService.getFormDataById(form.getTableName(), id);
        return formData != null ? ApiResponse.success(formData) : ApiResponse.error(404, "Record not found");
    }

    /**
     * Delete form data by id
     * @param formId form id
     * @param id record id
     * @return ApiResponse with success message
     */
    @DeleteMapping("/{formId}/{id}")
    public ApiResponse<String> deleteFormDataById(@PathVariable Long formId, @PathVariable Object id) {
        Form form = formService.getById(formId);
        if (form == null) {
            return ApiResponse.error(404, "Form not found with id: " + formId);
        }
        boolean deleted = formDataService.deleteFormDataById(form.getTableName(), id);
        return deleted ? ApiResponse.success("Record deleted successfully") : ApiResponse.error(500, "Failed to delete record");
    }

    /**
     * List form data with pagination
     * @param formId form id
     * @param page page number
     * @param size page size
     * @return ApiResponse with form data list
     */
    @GetMapping("/{formId}")
    public ApiResponse<List<Map<String, Object>>> listFormData(@PathVariable Long formId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        Form form = formService.getById(formId);
        if (form == null) {
            return ApiResponse.error(404, "Form not found with id: " + formId);
        }
        List<Map<String, Object>> formDataList = formDataService.listFormData(form.getTableName(), page, size);
        return ApiResponse.success(formDataList);
    }
}