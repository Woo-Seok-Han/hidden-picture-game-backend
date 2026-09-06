package com.infectioncontrol.detective.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record ErrorAreaDto(
        Long id,
        @DecimalMin("0.0") @DecimalMax("1.0") double x,
        @DecimalMin("0.0") @DecimalMax("1.0") double y,
        @DecimalMin("0.0") @DecimalMax("1.0") double width,
        @DecimalMin("0.0") @DecimalMax("1.0") double height
) {
}
