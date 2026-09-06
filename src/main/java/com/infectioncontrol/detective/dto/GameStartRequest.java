package com.infectioncontrol.detective.dto;

import jakarta.validation.constraints.NotBlank;

public record GameStartRequest(
        @NotBlank String employeeNumber
) {
}
