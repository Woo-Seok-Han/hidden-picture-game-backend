package com.infectioncontrol.detective.dto;

import jakarta.validation.constraints.NotBlank;

public record EmployeeValidateRequest(
        @NotBlank String employeeNumber
) {
}
