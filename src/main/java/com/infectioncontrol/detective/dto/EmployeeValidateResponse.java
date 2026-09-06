package com.infectioncontrol.detective.dto;

public record EmployeeValidateResponse(
        boolean valid,
        String message
) {
}
