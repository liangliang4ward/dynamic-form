package com.zjjg.digitize.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjjg.digitize.common.ApiResponse;
import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.entity.FormField;
import com.zjjg.digitize.mapper.FormFieldMapper;
import com.zjjg.digitize.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    @Autowired
    private FormService formService;

    @Autowired
    private FormFieldMapper formFieldMapper;

    // Create a new form
    @PostMapping
    public ApiResponse<Form> createForm(@RequestBody Form form) {
        formService.save(form);
        return ApiResponse.success(form);
    }

    // Get form by id
    @GetMapping("/{id}")
    public ApiResponse<Form> getFormById(@PathVariable Long id) {
        Form form = formService.getById(id);
        return form != null ? ApiResponse.success(form) : ApiResponse.error(404, "Form not found");
    }

    // Get all forms
    @GetMapping
    public ApiResponse<List<Form>> getAllForms() {
        List<Form> forms = formService.list();
        return ApiResponse.success(forms);
    }

    // Update form
    @PutMapping("/{id}")
    public ApiResponse<Form> updateForm(@PathVariable Long id, @RequestBody Form form) {
        if (formService.getById(id) == null) {
            return ApiResponse.error(404, "Form not found");
        }
        form.setId(id);
        formService.updateById(form);
        return ApiResponse.success(form);
    }

    // Delete form
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteForm(@PathVariable Long id) {
        if (formService.getById(id) == null) {
            return ApiResponse.error(404, "Form not found");
        }
        formService.removeById(id);
        return ApiResponse.success();
    }

    // Add form fields
    @PostMapping("/{formId}/fields")
    public ApiResponse<List<FormField>> addFormFields(@PathVariable Long formId, @RequestBody List<FormField> fields) {
        fields.forEach(field -> field.setFormId(formId));
        fields.forEach(formFieldMapper::insert);
        return ApiResponse.success(fields);
    }

    // Get form fields
    @GetMapping("/{formId}/fields")
    public ApiResponse<List<FormField>> getFormFields(@PathVariable Long formId) {
        List<FormField> fields = formFieldMapper.selectByFormId(formId);
        return ApiResponse.success(fields);
    }

    // Publish form
    @PostMapping("/{formId}/publish")
    public ApiResponse<Boolean> publishForm(@PathVariable Long formId) {
        try {
            boolean result = formService.publishForm(formId);
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage(), false);
        } catch (SQLException e) {
            return ApiResponse.error(500, e.getMessage(), false);
        }
    }

    // Create form template
    @PostMapping("/templates")
    public ApiResponse<Form> createFormTemplate(@RequestBody Form form) {
        form.setIsTemplate(true);
        formService.save(form);
        return ApiResponse.success(form);
    }

    // Get form templates
    @GetMapping("/templates")
    public ApiResponse<List<Form>> getFormTemplates() {
        QueryWrapper<Form> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_template", true);
        List<Form> templates = formService.list(queryWrapper);
        return ApiResponse.success(templates);
    }

    // Get form template by code
    @GetMapping("/templates/{templateCode}")
    public ApiResponse<Form> getFormTemplateByCode(@PathVariable String templateCode) {
        QueryWrapper<Form> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_template", true);
        queryWrapper.eq("template_code", templateCode);
        List<Form> templates = formService.list(queryWrapper);
        return templates.isEmpty() ? ApiResponse.error(404, "Template not found") : ApiResponse.success(templates.get(0));
    }

    // Create form from template
    @PostMapping("/from-template/{templateCode}")
    public ApiResponse<Form> createFormFromTemplate(@PathVariable String templateCode, @RequestBody Form form) {
        QueryWrapper<Form> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_template", true);
        queryWrapper.eq("template_code", templateCode);
        List<Form> templates = formService.list(queryWrapper);
        if (templates.isEmpty()) {
            return ApiResponse.error(404, "Template not found");
        }
        Form template = templates.get(0);
        // Create new form with template fields
        form.setIsTemplate(false);
        form.setStatus("draft");
        formService.save(form);
        
        // Copy fields from template
        List<FormField> templateFields = formFieldMapper.selectByFormId(template.getId());
        for (FormField field : templateFields) {
            FormField newField = new FormField();
            newField.setFormId(form.getId());
            newField.setFieldName(field.getFieldName());
            newField.setFieldType(field.getFieldType());
            newField.setFieldLabel(field.getFieldLabel());
            newField.setFieldLength(field.getFieldLength());
            newField.setPrimaryKey(field.isPrimaryKey());
            newField.setNotNull(field.isNotNull());
            newField.setDefaultValue(field.getDefaultValue());
            newField.setSortOrder(field.getSortOrder());
            newField.setValidationRules(field.getValidationRules());
            formFieldMapper.insert(newField);
        }
        
        return ApiResponse.success(form);
    }
}