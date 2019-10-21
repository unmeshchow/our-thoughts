package com.unmeshc.ourthoughts.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class IndexControllerTest {

    private IndexController controller;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        controller = new IndexController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/index.html"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"));
    }
}