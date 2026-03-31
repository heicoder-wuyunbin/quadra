package com.quadra.system.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quadra.system.adapter.out.persistence.entity.SysDataAnalysisDO;
import com.quadra.system.adapter.out.persistence.mapper.SysDataAnalysisMapper;
import com.quadra.system.application.port.out.AnalysisRepositoryPort;
import com.quadra.system.domain.model.SysDataAnalysis;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnalysisRepositoryImpl implements AnalysisRepositoryPort {

    private final SysDataAnalysisMapper sysDataAnalysisMapper;

    public AnalysisRepositoryImpl(SysDataAnalysisMapper sysDataAnalysisMapper) {
        this.sysDataAnalysisMapper = sysDataAnalysisMapper;
    }

    @Override
    public Long nextId() {
        return System.currentTimeMillis();
    }

    @Override
    public void save(SysDataAnalysis analysis) {
        SysDataAnalysisDO analysisDO = toAnalysisDO(analysis);
        sysDataAnalysisMapper.insert(analysisDO);
    }

    @Override
    public void update(SysDataAnalysis analysis) {
        SysDataAnalysisDO analysisDO = toAnalysisDO(analysis);
        sysDataAnalysisMapper.updateById(analysisDO);
    }

    @Override
    public SysDataAnalysis findById(Long id) {
        SysDataAnalysisDO analysisDO = sysDataAnalysisMapper.selectById(id);
        if (analysisDO == null) {
            return null;
        }
        return toAnalysis(analysisDO);
    }

    @Override
    public SysDataAnalysis findByRecordDate(LocalDate date) {
        LambdaQueryWrapper<SysDataAnalysisDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataAnalysisDO::getRecordDate, date);
        
        SysDataAnalysisDO analysisDO = sysDataAnalysisMapper.selectOne(wrapper);
        if (analysisDO == null) {
            return null;
        }
        return toAnalysis(analysisDO);
    }

    @Override
    public List<SysDataAnalysis> findByDateRange(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SysDataAnalysisDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(SysDataAnalysisDO::getRecordDate, startDate, endDate)
               .orderByDesc(SysDataAnalysisDO::getRecordDate);
        
        List<SysDataAnalysisDO> list = sysDataAnalysisMapper.selectList(wrapper);
        List<SysDataAnalysis> result = new ArrayList<>();
        for (SysDataAnalysisDO analysisDO : list) {
            result.add(toAnalysis(analysisDO));
        }
        return result;
    }

    private SysDataAnalysisDO toAnalysisDO(SysDataAnalysis analysis) {
        SysDataAnalysisDO analysisDO = new SysDataAnalysisDO();
        analysisDO.setId(analysis.getId());
        analysisDO.setRecordDate(analysis.getRecordDate());
        analysisDO.setNumRegistered(analysis.getNumRegistered());
        analysisDO.setNumActive(analysis.getNumActive());
        analysisDO.setNumMovement(analysis.getNumMovement());
        analysisDO.setNumMatched(analysis.getNumMatched());
        analysisDO.setNumRetention1d(analysis.getNumRetention1d());
        analysisDO.setVersion(analysis.getVersion());
        return analysisDO;
    }

    private SysDataAnalysis toAnalysis(SysDataAnalysisDO analysisDO) {
        try {
            java.lang.reflect.Constructor<SysDataAnalysis> constructor = SysDataAnalysis.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            SysDataAnalysis analysis = constructor.newInstance();
            
            java.lang.reflect.Field idField = SysDataAnalysis.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(analysis, analysisDO.getId());

            java.lang.reflect.Field recordDateField = SysDataAnalysis.class.getDeclaredField("recordDate");
            recordDateField.setAccessible(true);
            recordDateField.set(analysis, analysisDO.getRecordDate());

            java.lang.reflect.Field numRegisteredField = SysDataAnalysis.class.getDeclaredField("numRegistered");
            numRegisteredField.setAccessible(true);
            numRegisteredField.set(analysis, analysisDO.getNumRegistered());

            java.lang.reflect.Field numActiveField = SysDataAnalysis.class.getDeclaredField("numActive");
            numActiveField.setAccessible(true);
            numActiveField.set(analysis, analysisDO.getNumActive());

            java.lang.reflect.Field numMovementField = SysDataAnalysis.class.getDeclaredField("numMovement");
            numMovementField.setAccessible(true);
            numMovementField.set(analysis, analysisDO.getNumMovement());

            java.lang.reflect.Field numMatchedField = SysDataAnalysis.class.getDeclaredField("numMatched");
            numMatchedField.setAccessible(true);
            numMatchedField.set(analysis, analysisDO.getNumMatched());

            java.lang.reflect.Field numRetention1dField = SysDataAnalysis.class.getDeclaredField("numRetention1d");
            numRetention1dField.setAccessible(true);
            numRetention1dField.set(analysis, analysisDO.getNumRetention1d());

            java.lang.reflect.Field versionField = SysDataAnalysis.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(analysis, analysisDO.getVersion());

            return analysis;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore SysDataAnalysis from DB", e);
        }
    }
}
