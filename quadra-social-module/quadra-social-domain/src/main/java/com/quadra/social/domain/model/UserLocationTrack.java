package com.quadra.social.domain.model;

import com.quadra.social.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户位置轨迹实体
 * 用于记录用户的历史位置信息，支持"附近的人"功能
 * 说明：高频位置更新使用 Redis GEO，此实体用于持久化归档
 */
public class UserLocationTrack {
    private Long id;
    private Long userId;
    private BigDecimal longitude;    // 经度
    private BigDecimal latitude;     // 纬度
    private String address;          // 地址描述
    private LocalDateTime locateTime; // 定位时间
    private Integer version;
    private Integer deleted;

    // 禁用默认无参构造
    private UserLocationTrack() {}

    /**
     * 工厂方法：创建位置轨迹记录
     * @param id 主键 ID
     * @param userId 用户 ID
     * @param longitude 经度
     * @param latitude 纬度
     * @param address 地址描述
     */
    public static UserLocationTrack record(Long id, Long userId, BigDecimal longitude, BigDecimal latitude, String address) {
        if (id == null || id <= 0) {
            throw new DomainException("轨迹 ID 必须有效");
        }
        if (userId == null || userId <= 0) {
            throw new DomainException("用户 ID 必须有效");
        }
        if (longitude == null || latitude == null) {
            throw new DomainException("经纬度不能为空");
        }

        UserLocationTrack track = new UserLocationTrack();
        track.id = id;
        track.userId = userId;
        track.longitude = longitude;
        track.latitude = latitude;
        track.address = address;
        track.locateTime = LocalDateTime.now();
        track.version = 0;
        track.deleted = 0;

        return track;
    }

    /**
     * 更新位置信息
     * @param longitude 经度
     * @param latitude 纬度
     * @param address 地址描述
     */
    public void updateLocation(BigDecimal longitude, BigDecimal latitude, String address) {
        if (longitude == null || latitude == null) {
            throw new DomainException("经纬度不能为空");
        }
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.locateTime = LocalDateTime.now();
    }

    /**
     * 逻辑删除轨迹记录
     */
    public void delete() {
        if (this.deleted == 1) {
            throw new DomainException("轨迹记录已被删除");
        }
        this.deleted = 1;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getLongitude() { return longitude; }
    public BigDecimal getLatitude() { return latitude; }
    public String getAddress() { return address; }
    public LocalDateTime getLocateTime() { return locateTime; }
    public Integer getVersion() { return version; }
    public Integer getDeleted() { return deleted; }
}
