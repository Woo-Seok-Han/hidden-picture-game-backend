package com.infectioncontrol.detective.dto;

import jakarta.validation.constraints.NotBlank;

public record GameCompleteRequest(
        @NotBlank String sessionId
) {
}
