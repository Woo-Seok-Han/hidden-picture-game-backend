package com.infectioncontrol.detective.dto;

public record QuestionDetailResponse(
        String questionId,
        int questionNumber,
        String title,
        String imageUrl,
        String userAnswer,
        String correctAnswer,
        boolean isCorrect,
        String explanation,
        SelectedPointDto selectedPoint
) {
}
