package com.zjjg.digitize.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.entity.FormField;
import com.zjjg.digitize.mapper.FormFieldMapper;
import com.zjjg.digitize.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Form> createForm(@RequestBody Form form) {
        formService.save(form);
        return new ResponseEntity<>(form, HttpStatus.CREATED);
    }

    // Get form by id
    @GetMapping("/{id}")
    public ResponseEntity<Form> getFormById(@PathVariable Long id) {
        Form form = formService.getById(id);
        return form != null ? ResponseEntity.ok(form) : ResponseEntity.notFound().build();
    }

    // Get all forms
    @GetMapping
    public ResponseEntity<List<Form>> getAllForms() {
        List<Form> forms = formService.list();
        return ResponseEntity.ok(forms);
    }

    // Update form
    @PutMapping("/{id}")
    public ResponseEntity<Form> updateForm(@PathVariable Long id, @RequestBody Form form) {
        if (formService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        form.setId(id);
        formService.updateById(form);
        return ResponseEntity.ok(form);
    }

    // Delete form
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        if (formService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        formService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    // Add form fields
    @PostMapping("/{formId}/fields")
    public ResponseEntity<List<FormField>> addFormFields(@PathVariable Long formId, @RequestBody List<FormField> fields) {
        fields.forEach(field -> field.setFormId(formId));
        fields.forEach(formFieldMapper::insert);
        return new ResponseEntity<>(fields, HttpStatus.CREATED);
    }

    // Get form fields
    @GetMapping("/{formId}/fields")
    public ResponseEntity<List<FormField>> getFormFields(@PathVariable Long formId) {
        List<FormField> fields = formFieldMapper.selectByFormId(formId);
        return ResponseEntity.ok(fields);
    }

    // Publish form
    @PostMapping("/{formId}/publish")
    public ResponseEntity<Boolean> publishForm(@PathVariable Long formId) {
        try {
            boolean result = formService.publishForm(formId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(false);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(false);
        }
    }
}