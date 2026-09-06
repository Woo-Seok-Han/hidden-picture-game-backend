package com.infectioncontrol.detective.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record GameSubmitRequest(
        @NotBlank String sessionId,
        @Valid List<AnswerRequest> answers
) {
}
