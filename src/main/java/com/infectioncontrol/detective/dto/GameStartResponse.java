package com.infectioncontrol.detective.dto;

import java.time.Instant;

public record GameStartResponse(
        String sessionId,
        String employeeNumber,
        Instant startTime
) {
}
