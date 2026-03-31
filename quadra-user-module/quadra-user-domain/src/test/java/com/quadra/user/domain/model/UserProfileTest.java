package com.quadra.user.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserProfile 实体单元测试
 */
@DisplayName("UserProfile 实体测试")
class UserProfileTest {

    @Test
    @DisplayName("创建用户资料成功")
    void createUserProfileSuccessfully() {
        // Given
        Long userId = 1L;

        // When
        UserProfile profile = new UserProfile(userId);
        profile.updateBaseInfo("测试用户", 1, null, null, null, null, null, null, null, null);

        // Then
        assertNotNull(profile);
        assertEquals(userId, profile.getId());
        assertEquals("测试用户", profile.getNickname());
        assertEquals(1, profile.getGender());
    }

    @Test
    @DisplayName("更新用户资料成功")
    void updateUserProfileSuccessfully() {
        // Given
        UserProfile profile = new UserProfile(1L);
        profile.updateBaseInfo("旧昵称", 0, null, null, null, null, null, null, null, null);

        // When
        Map<String, Object> tags = new HashMap<>();
        profile.updateBaseInfo("新昵称", 1, LocalDate.of(1990, 1, 1), "北京", "avatar.jpg", "10000", "工程师", 0, "cover.jpg", tags);

        // Then
        assertEquals("新昵称", profile.getNickname());
        assertEquals(1, profile.getGender());
        assertEquals("北京", profile.getCity());
        assertNotNull(profile.getTags());
    }
}
