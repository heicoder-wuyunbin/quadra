package com.quadra.user.domain.model;

public class UserSetting {
    private Long id;
    private Integer likeNotification; // 0-关闭, 1-开启
    private Integer commentNotification;
    private Integer systemNotification;

    public UserSetting(Long id) {
        this.id = id;
        this.likeNotification = 1;
        this.commentNotification = 1;
        this.systemNotification = 1;
    }

    public void updateNotification(Integer like, Integer comment, Integer system) {
        if (like != null) {
            this.likeNotification = like;
        }
        if (comment != null) {
            this.commentNotification = comment;
        }
        if (system != null) {
            this.systemNotification = system;
        }
    }

    // getters
    public Long getId() { return id; }
    public Integer getLikeNotification() { return likeNotification; }
    public Integer getCommentNotification() { return commentNotification; }
    public Integer getSystemNotification() { return systemNotification; }
}
