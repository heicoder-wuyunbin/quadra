package com.quadra.social.domain.model;

import com.quadra.social.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserFollow 聚合根单元测试
 */
@DisplayName("UserFollow 聚合根测试")
class UserFollowTest {

    @Test
    @DisplayName("关注用户成功")
    void followUserSuccessfully() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        Long targetUserId = 200L;

        // When
        UserFollow userFollow = UserFollow.follow(id, userId, targetUserId);

        // Then
        assertNotNull(userFollow);
        assertEquals(userId, userFollow.getUserId());
        assertEquals(targetUserId, userFollow.getTargetUserId());
        assertEquals(0, userFollow.getDeleted());
    }

    @Test
    @DisplayName("关注自己应抛出异常")
    void followSelfShouldThrowException() {
        // Then
        assertThrows(DomainException.class, 
            () -> UserFollow.follow(1L, 100L, 100L));
    }

    @Test
    @DisplayName("取消关注成功")
    void unfollowSuccessfully() {
        // Given
        UserFollow userFollow = UserFollow.follow(1L, 100L, 200L);

        // When
        userFollow.unfollow();

        // Then
        assertEquals(1, userFollow.getDeleted());
    }
}
