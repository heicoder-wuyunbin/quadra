package com.quadra.system.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.system.adapter.out.persistence.entity.SysDataAnalysisDO;
import com.quadra.system.adapter.out.persistence.mapper.SysDataAnalysisMapper;
import com.quadra.system.application.port.in.dto.DailyAnalysisDTO;
import com.quadra.system.application.port.out.AnalysisQueryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnalysisQueryRepositoryImpl implements AnalysisQueryPort {

    private final SysDataAnalysisMapper sysDataAnalysisMapper;

    public AnalysisQueryRepositoryImpl(SysDataAnalysisMapper sysDataAnalysisMapper) {
        this.sysDataAnalysisMapper = sysDataAnalysisMapper;
    }

    @Override
    public DailyAnalysisDTO findByDate(LocalDate date) {
        LambdaQueryWrapper<SysDataAnalysisDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataAnalysisDO::getRecordDate, date);
        
        SysDataAnalysisDO analysisDO = sysDataAnalysisMapper.selectOne(wrapper);
        if (analysisDO == null) {
            return null;
        }
        return toDTO(analysisDO);
    }

    @Override
    public List<DailyAnalysisDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SysDataAnalysisDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SysDataAnalysisDO::getRecordDate, startDate, endDate)
               .orderByDesc(SysDataAnalysisDO::getRecordDate);
        
        List<SysDataAnalysisDO> list = sysDataAnalysisMapper.selectList(wrapper);
        List<DailyAnalysisDTO> result = new ArrayList<>();
        for (SysDataAnalysisDO analysisDO : list) {
            result.add(toDTO(analysisDO));
        }
        return result;
    }

    private DailyAnalysisDTO toDTO(SysDataAnalysisDO analysisDO) {
        return new DailyAnalysisDTO(
                analysisDO.getId(),
                analysisDO.getRecordDate(),
                analysisDO.getNumRegistered(),
                analysisDO.getNumActive(),
                analysisDO.getNumMovement(),
                analysisDO.getNumMatched(),
                analysisDO.getNumRetention1d()
        );
    }
}
