package com.infectioncontrol.detective.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record AnswerRequest(
        @NotBlank String questionId,
        Integer questionNumber,
        SelectedPointDto selectedPoint,
        boolean hasError,
        Instant timestamp
) {
}
