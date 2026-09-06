package com.infectioncontrol.detective.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void startGameCreatesSession() throws Exception {
        mockMvc.perform(post("/api/game/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeNumber\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").isString())
                .andExpect(jsonPath("$.employeeNumber").value("123456"));
    }

    @Test
    void questionsReturnsSeededQuestions() throws Exception {
        mockMvc.perform(get("/api/game/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id").value("q1"))
                .andExpect(jsonPath("$[0].imageUrl").value("http://test-api.example.com/sample/questions/q1.svg"));
    }
}
