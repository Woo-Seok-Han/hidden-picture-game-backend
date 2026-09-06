package com.infectioncontrol.detective.dto;

import java.time.Instant;
import java.util.List;

public record AdminQuestionResponse(
        String id,
        int questionNumber,
        String imageUrl,
        String imageAlt,
        String explanation,
        int timeLimitSeconds,
        boolean active,
        List<ErrorAreaDto> errorAreas,
        Instant createdAt,
        Instant updatedAt
) {
}
