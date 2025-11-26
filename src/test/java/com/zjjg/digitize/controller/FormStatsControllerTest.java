package com.zjjg.digitize.controller;

import com.zjjg.digitize.entity.Form;
import com.zjjg.digitize.service.FormService;
import com.zjjg.digitize.service.FormStatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FormStatsController.class)
public class FormStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormStatsService formStatsService;

    @MockBean
    private FormService formService;

    @Test
    public void testGetFormStats() throws Exception {
        Form form = new Form();
        form.setId(1L);
        form.setTableName("test_form");
        form.setStatus("published");

        when(formService.getById(1L)).thenReturn(form);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", 100);
        stats.put("createdAtMin", "2023-01-01");
        stats.put("createdAtMax", "2023-12-31");

        when(formStatsService.getFormStats("test_form")).thenReturn(stats);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/form-stats/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalRecords").value(100));
    }

    @Test
    public void testGetFieldStats() throws Exception {
        Form form = new Form();
        form.setId(1L);
        form.setTableName("test_form");
        form.setStatus("published");

        when(formService.getById(1L)).thenReturn(form);

        Map<String, Object> fieldStats = new HashMap<>();
        fieldStats.put("fieldType", "string");
        fieldStats.put("nonNullCount", 80);
        fieldStats.put("distinctCount", 50);

        when(formStatsService.getFieldStats("test_form", "field1")).thenReturn(fieldStats);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/form-stats/1/field/field1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fieldType").value("string"));
    }
}