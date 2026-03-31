package com.quadra.system.application.port.in;

import java.time.LocalDate;

public interface GenerateDailyAnalysisUseCase {
    void generateDailyAnalysis(LocalDate date);
}
