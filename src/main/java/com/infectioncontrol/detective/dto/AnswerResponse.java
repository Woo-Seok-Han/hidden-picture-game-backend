package com.infectioncontrol.detective.dto;

import java.time.Instant;

public record AnswerResponse(
        String questionId,
        Integer questionNumber,
        SelectedPointDto selectedPoint,
        boolean hasError,
        boolean correct,
        Instant timestamp
) {
}
