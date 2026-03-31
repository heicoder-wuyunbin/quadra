package com.quadra.system.application.port.out;

import com.quadra.system.domain.model.SysDataAnalysis;
import java.time.LocalDate;
import java.util.List;

public interface AnalysisRepositoryPort {
    Long nextId();
    void save(SysDataAnalysis analysis);
    void update(SysDataAnalysis analysis);
    SysDataAnalysis findById(Long id);
    SysDataAnalysis findByRecordDate(LocalDate date);
    List<SysDataAnalysis> findByDateRange(LocalDate startDate, LocalDate endDate);
}
