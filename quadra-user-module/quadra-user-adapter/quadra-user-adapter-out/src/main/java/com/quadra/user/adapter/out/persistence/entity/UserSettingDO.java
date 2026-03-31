package com.quadra.user.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

@TableName("user_setting")
public class UserSettingDO {
    @TableId(type = IdType.INPUT)
    private Long id;
    private Integer likeNotification;
    private Integer commentNotification;
    private Integer systemNotification;
    
    @Version
    private Integer version;
    private Integer deleted;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLikeNotification() { return likeNotification; }
    public void setLikeNotification(Integer likeNotification) { this.likeNotification = likeNotification; }
    public Integer getCommentNotification() { return commentNotification; }
    public void setCommentNotification(Integer commentNotification) { this.commentNotification = commentNotification; }
    public Integer getSystemNotification() { return systemNotification; }
    public void setSystemNotification(Integer systemNotification) { this.systemNotification = systemNotification; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
