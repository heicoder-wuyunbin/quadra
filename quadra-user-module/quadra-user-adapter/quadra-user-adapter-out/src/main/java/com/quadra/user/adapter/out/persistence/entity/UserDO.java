package com.quadra.user.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

@TableName("user")
public class UserDO {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String mobile;
    private String password;
    private String hxUser;
    private String hxPassword;
    private Integer status;
    @Version
    private Integer version;
    private Integer deleted;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getHxUser() { return hxUser; }
    public void setHxUser(String hxUser) { this.hxUser = hxUser; }
    public String getHxPassword() { return hxPassword; }
    public void setHxPassword(String hxPassword) { this.hxPassword = hxPassword; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
