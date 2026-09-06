package com.infectioncontrol.detective.controller;

import com.infectioncontrol.detective.dto.GameCompleteRequest;
import com.infectioncontrol.detective.dto.GameResultResponse;
import com.infectioncontrol.detective.dto.GameStartRequest;
import com.infectioncontrol.detective.dto.GameStartResponse;
import com.infectioncontrol.detective.dto.GameSubmitRequest;
import com.infectioncontrol.detective.dto.QuestionSummaryResponse;
import com.infectioncontrol.detective.service.GameService;
import com.infectioncontrol.detective.service.QuestionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final QuestionService questionService;

    public GameController(GameService gameService, QuestionService questionService) {
        this.gameService = gameService;
        this.questionService = questionService;
    }

    @PostMapping("/start")
    public GameStartResponse start(@Valid @RequestBody GameStartRequest request) {
        return gameService.start(request.employeeNumber());
    }

    @GetMapping("/questions")
    public List<QuestionSummaryResponse> questions() {
        return questionService.getPlayableQuestions();
    }

    @PostMapping("/submit")
    public GameResultResponse submit(@Valid @RequestBody GameSubmitRequest request) {
        return gameService.submit(request.sessionId(), request.answers());
    }

    @PostMapping("/complete")
    public GameResultResponse complete(@Valid @RequestBody GameCompleteRequest request) {
        return gameService.complete(request.sessionId());
    }
}
