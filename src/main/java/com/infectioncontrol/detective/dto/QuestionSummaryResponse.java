package com.infectioncontrol.detective.dto;

public record QuestionSummaryResponse(
        String id,
        int questionNumber,
        String imageUrl,
        String imageAlt,
        int timeLimitSeconds
) {
}
