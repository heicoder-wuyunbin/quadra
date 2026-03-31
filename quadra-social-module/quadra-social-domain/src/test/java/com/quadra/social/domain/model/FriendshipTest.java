package com.quadra.social.domain.model;

import com.quadra.social.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Friendship 聚合根单元测试
 */
@DisplayName("Friendship 聚合根测试")
class FriendshipTest {

    @Test
    @DisplayName("创建好友关系成功")
    void createFriendshipSuccessfully() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        Long friendId = 200L;

        // When
        Friendship friendship = Friendship.create(id, userId, friendId);

        // Then
        assertNotNull(friendship);
        assertEquals(userId, friendship.getUserId());
        assertEquals(friendId, friendship.getFriendId());
    }

    @Test
    @DisplayName("与自己成为好友应抛出异常")
    void beFriendWithSelfShouldThrowException() {
        // Then
        assertThrows(DomainException.class, 
            () -> Friendship.create(1L, 100L, 100L));
    }
}
