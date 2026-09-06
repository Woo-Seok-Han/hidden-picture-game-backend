package com.infectioncontrol.detective.dto;

import java.time.Instant;
import java.util.List;

public record DetailedGameResultResponse(
        String sessionId,
        String employeeNumber,
        List<AnswerResponse> answers,
        int correctAnswers,
        int totalQuestions,
        double accuracy,
        String totalTime,
        Instant completedAt,
        List<QuestionDetailResponse> details
) {
}
