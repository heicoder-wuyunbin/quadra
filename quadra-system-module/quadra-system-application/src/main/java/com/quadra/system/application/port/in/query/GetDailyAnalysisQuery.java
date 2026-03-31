package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.DailyAnalysisDTO;
import java.time.LocalDate;
import java.util.List;

public interface GetDailyAnalysisQuery {
    DailyAnalysisDTO getByDate(LocalDate date);
    List<DailyAnalysisDTO> getByDateRange(LocalDate startDate, LocalDate endDate);
}
