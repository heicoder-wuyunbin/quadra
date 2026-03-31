package com.quadra.user.domain.model;

import com.quadra.user.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User 聚合根单元测试
 */
@DisplayName("User 聚合根测试")
class UserTest {

    @Test
    @DisplayName("注册用户成功")
    void registerUserSuccessfully() {
        // Given
        Long id = 1L;
        String mobile = "13800138000";
        String encryptedPassword = "bcrypt_encoded_password";

        // When
        User user = User.register(id, mobile, encryptedPassword);

        // Then
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(mobile, user.getMobile());
        assertEquals(0, user.getDeleted());
        assertFalse(user.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("注册用户 - 手机号为空应抛出异常")
    void registerUserWithEmptyMobile() {
        // Then
        assertThrows(DomainException.class, 
            () -> User.register(1L, "", "pwd"));
    }

    @Test
    @DisplayName("更新用户资料成功")
    void updateUserProfileSuccessfully() {
        // Given
        User user = User.register(1L, "13800138000", "pwd");

        // When
        user.updateProfile("昵称", 1, null, "北京", null, null, null, null, null, null);

        // Then
        assertNotNull(user.getProfile());
        assertEquals("昵称", user.getProfile().getNickname());
    }
}
