package com.quadra.user.application.port.in.dto;

import java.time.LocalDate;

/**
 * 用户资料展示 DTO (读模型)
 */
public class UserProfileDTO {
    private Long userId;
    private String mobile;
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String city;
    private String income;
    private String profession;
    private Integer marriage;
    private String coverPic;
    private String tags;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
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
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
