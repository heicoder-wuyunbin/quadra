package com.quadra.system.application.port.out;

import com.quadra.system.application.port.in.dto.DailyAnalysisDTO;
import java.time.LocalDate;
import java.util.List;

public interface AnalysisQueryPort {
    DailyAnalysisDTO findByDate(LocalDate date);
    List<DailyAnalysisDTO> findByDateRange(LocalDate startDate, LocalDate endDate);
}
