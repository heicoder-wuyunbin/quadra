package com.quadra.user.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.time.LocalDate;
import java.util.Map;

@TableName(value = "user_profile", autoResultMap = true)
public class UserProfileDO {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String city;
    private String income;
    private String profession;
    private Integer marriage;
    private String coverPic;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> tags;
    
    @Version
    private Integer version;
    private Integer deleted;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getIncome() { return income; }
    public void setIncome(String income) { this.income = income; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public Integer getMarriage() { return marriage; }
    public void setMarriage(Integer marriage) { this.marriage = marriage; }
    public String getCoverPic() { return coverPic; }
    public void setCoverPic(String coverPic) { this.coverPic = coverPic; }
    public Map<String, Object> getTags() { return tags; }
    public void setTags(Map<String, Object> tags) { this.tags = tags; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
