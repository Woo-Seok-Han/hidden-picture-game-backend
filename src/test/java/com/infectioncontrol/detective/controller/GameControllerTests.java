package com.infectioncontrol.detective.controller;

import static org.hamcrest.Matchers.hasSize;
import com.infectioncontrol.detective.domain.Question;
import com.infectioncontrol.detective.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:game-controller-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
class GameControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        questionRepository.deleteAll();
        for (int index = 1; index <= 6; index++) {
            questionRepository.save(new Question(
                    "test-q" + index,
                    index,
                    "/uploads/test-q" + index + ".png",
                    "테스트 문제 " + index,
                    "테스트 해설 " + index,
                    15
            ));
        }
    }

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
    void questionsReturnsRandomFiveActiveQuestions() throws Exception {
        mockMvc.perform(get("/api/game/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }
}
