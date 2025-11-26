package com.zjjg.digitize.controller;

import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.service.FormDataService;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FormDataController.class)
public class FormDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormDataService formDataService;

    @MockBean
    private FormService formService;

    @Test
    public void testSaveFormData() throws Exception {
        Form form = new Form();
        form.setId(1L);
        form.setTableName("test_form");
        form.setStatus("published");

        when(formService.getById(1L)).thenReturn(form);

        Map<String, Object> formData = new HashMap<>();
        formData.put("field1", "value1");
        formData.put("field2", "value2");

        when(formDataService.saveFormData("test_form", formData)).thenReturn(formData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/form-data/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"field1\": \"value1\", \"field2\": \"value2\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Form data saved successfully"));
    }

    @Test
    public void testListFormData() throws Exception {
        Form form = new Form();
        form.setId(1L);
        form.setTableName("test_form");
        form.setStatus("published");

        when(formService.getById(1L)).thenReturn(form);

        Map<String, Object> formData = new HashMap<>();
        formData.put("id", 1L);
        formData.put("field1", "value1");

        when(formDataService.listFormData("test_form", 1, 10)).thenReturn(Collections.singletonList(formData));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/form-data/1?page=1&size=10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].field1").value("value1"));
    }
}