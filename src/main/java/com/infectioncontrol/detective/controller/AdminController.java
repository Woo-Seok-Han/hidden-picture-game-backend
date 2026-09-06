package com.infectioncontrol.detective.controller;

import com.infectioncontrol.detective.dto.AdminQuestionResponse;
import com.infectioncontrol.detective.dto.DetailedGameResultResponse;
import com.infectioncontrol.detective.service.GameService;
import com.infectioncontrol.detective.service.QuestionService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final QuestionService questionService;
    private final GameService gameService;

    public AdminController(QuestionService questionService, GameService gameService) {
        this.questionService = questionService;
        this.gameService = gameService;
    }

    @GetMapping("/questions")
    public List<AdminQuestionResponse> questions() {
        return questionService.getAdminQuestions();
    }

    @PostMapping(value = "/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AdminQuestionResponse createQuestion(
            @RequestParam MultipartFile image,
            @RequestParam(required = false) String imageAlt,
            @RequestParam(required = false) String explanation,
            @RequestParam(defaultValue = "30") int timeLimitSeconds,
            @RequestParam(defaultValue = "[]") String errorAreas
    ) {
        return questionService.createQuestion(image, imageAlt, explanation, timeLimitSeconds, errorAreas);
    }

    @PutMapping(value = "/questions/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AdminQuestionResponse updateQuestion(
            @PathVariable String id,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String imageAlt,
            @RequestParam(required = false) String explanation,
            @RequestParam(defaultValue = "30") int timeLimitSeconds,
            @RequestParam(defaultValue = "[]") String errorAreas
    ) {
        return questionService.updateQuestion(id, image, imageAlt, explanation, timeLimitSeconds, errorAreas);
    }

    @DeleteMapping("/questions/{id}")
    public void deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
    }

    @PatchMapping("/questions/{id}/active")
    public AdminQuestionResponse changeActive(
            @PathVariable String id,
            @RequestParam boolean active
    ) {
        return questionService.changeActive(id, active);
    }

    @GetMapping("/results")
    public List<DetailedGameResultResponse> results() {
        return gameService.getAllResults();
    }
}
