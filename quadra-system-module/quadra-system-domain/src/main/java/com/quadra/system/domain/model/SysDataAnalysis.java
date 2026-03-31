package com.quadra.system.domain.model;

import com.quadra.system.domain.exception.DomainException;

import java.time.LocalDate;

/**
 * 每日核心数据统计聚合根
 */
public class SysDataAnalysis {
    
    private Long id;
    private LocalDate recordDate; // 统计归属日期
    private Integer numRegistered; // 新注册用户数
    private Integer numActive; // 日活跃用户数 DAU
    private Integer numMovement; // 新增动态数
    private Integer numMatched; // 互相喜欢匹配成功对数
    private Integer numRetention1d; // 次日留存用户数
    private Integer version;

    // 禁用默认无参构造
    private SysDataAnalysis() {}

    /**
     * 工厂方法：生成每日统计
     */
    public static SysDataAnalysis generate(Long id, LocalDate recordDate, 
                                            Integer numRegistered, Integer numActive, 
                                            Integer numMovement, Integer numMatched, 
                                            Integer numRetention1d) {
        if (id == null || id <= 0) {
            throw new DomainException("统计ID必须有效");
        }
        if (recordDate == null) {
            throw new DomainException("统计日期不能为空");
        }

        SysDataAnalysis analysis = new SysDataAnalysis();
        analysis.id = id;
        analysis.recordDate = recordDate;
        analysis.numRegistered = numRegistered != null ? numRegistered : 0;
        analysis.numActive = numActive != null ? numActive : 0;
        analysis.numMovement = numMovement != null ? numMovement : 0;
        analysis.numMatched = numMatched != null ? numMatched : 0;
        analysis.numRetention1d = numRetention1d != null ? numRetention1d : 0;
        analysis.version = 0;

        return analysis;
    }

    /**
     * 更新统计数据
     */
    public void update(Integer numRegistered, Integer numActive, Integer numMovement, 
                       Integer numMatched, Integer numRetention1d) {
        if (numRegistered != null) {
            this.numRegistered = numRegistered;
        }
        if (numActive != null) {
            this.numActive = numActive;
        }
        if (numMovement != null) {
            this.numMovement = numMovement;
        }
        if (numMatched != null) {
            this.numMatched = numMatched;
        }
        if (numRetention1d != null) {
            this.numRetention1d = numRetention1d;
        }
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getRecordDate() { return recordDate; }
    public Integer getNumRegistered() { return numRegistered; }
    public Integer getNumActive() { return numActive; }
    public Integer getNumMovement() { return numMovement; }
    public Integer getNumMatched() { return numMatched; }
    public Integer getNumRetention1d() { return numRetention1d; }
    public Integer getVersion() { return version; }
}
