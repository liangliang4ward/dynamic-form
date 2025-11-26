package com.zjjg.digitize.controller;

import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.entity.FormField;
import com.zjjg.digitize.service.FormService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FormController.class)
public class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormService formService;

    @Test
    public void testCreateForm() throws Exception {
        Form form = new Form();
        form.setName("Test Form");
        form.setTableName("test_form");

        when(formService.save(form)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/forms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Form\", \"tableName\": \"test_form\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Form created successfully"));
    }

    @Test
    public void testAddFormField() throws Exception {
        FormField field = new FormField();
        field.setFormId(1L);
        field.setFieldName("test_field");
        field.setFieldType("string");
        field.setFieldLabel("Test Field");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/forms/1/fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test_field\", \"type\": \"string\", \"label\": \"Test Field\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Field added successfully"));
    }
}