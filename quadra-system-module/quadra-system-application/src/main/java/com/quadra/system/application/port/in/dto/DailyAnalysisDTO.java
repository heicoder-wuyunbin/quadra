package com.quadra.system.application.port.in.dto;

import java.time.LocalDate;

public record DailyAnalysisDTO(
    Long id,
    LocalDate recordDate,
    Integer numRegistered,
    Integer numActive,
    Integer numMovement,
    Integer numMatched,
    Integer numRetention1d
) {}
