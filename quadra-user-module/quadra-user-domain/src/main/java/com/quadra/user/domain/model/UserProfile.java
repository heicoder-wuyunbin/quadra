package com.quadra.user.domain.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer gender; // 0-未知, 1-男, 2-女
    private LocalDate birthday;
    private String city;
    private String income;
    private String profession;
    private Integer marriage; // 0-未婚, 1-离异, 2-丧偶
    private String coverPic;
    private Map<String, Object> tags;

    public UserProfile(Long id) {
        this.id = id;
        this.gender = 0;
        this.marriage = 0;
        this.tags = new HashMap<>();
    }

    public void updateBaseInfo(String nickname, Integer gender, LocalDate birthday, String city, String avatar, String income, String profession, Integer marriage, String coverPic, Map<String, Object> tags) {
        if (nickname != null) this.nickname = nickname;
        if (gender != null) this.gender = gender;
        if (birthday != null) this.birthday = birthday;
        if (city != null) this.city = city;
        if (avatar != null) this.avatar = avatar;
        if (income != null) this.income = income;
        if (profession != null) this.profession = profession;
        if (marriage != null) this.marriage = marriage;
        if (coverPic != null) this.coverPic = coverPic;
        if (tags != null) this.tags = tags;
    }

    // getters
    public Long getId() { return id; }
    public String getNickname() { return nickname; }
    public String getAvatar() { return avatar; }
    public Integer getGender() { return gender; }
    public LocalDate getBirthday() { return birthday; }
    public String getCity() { return city; }
    public String getIncome() { return income; }
    public String getProfession() { return profession; }
    public Integer getMarriage() { return marriage; }
    public String getCoverPic() { return coverPic; }
    public Map<String, Object> getTags() { return tags; }
}
