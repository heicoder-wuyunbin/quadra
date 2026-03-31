package com.quadra.social.domain.model;

import com.quadra.social.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMatchLike 聚合根单元测试
 */
@DisplayName("UserMatchLike 聚合根测试")
class UserMatchLikeTest {

    @Test
    @DisplayName("滑动 - 喜欢成功")
    void swipeLikeSuccessfully() {
        // Given
        Long id = 1L;
        Long userId = 100L;
        Long targetUserId = 200L;
        UserMatchLike.ActionType actionType = UserMatchLike.ActionType.LIKE;

        // When
        UserMatchLike matchLike = UserMatchLike.swipe(id, userId, targetUserId, actionType);

        // Then
        assertNotNull(matchLike);
        assertEquals(userId, matchLike.getUserId());
        assertEquals(targetUserId, matchLike.getTargetUserId());
        assertEquals(UserMatchLike.ActionType.LIKE, matchLike.getActionType());
    }

    @Test
    @DisplayName("滑动 - 对自己滑动应抛出异常")
    void swipeOnSelfShouldThrowException() {
        // Then
        assertThrows(DomainException.class, 
            () -> UserMatchLike.swipe(1L, 100L, 100L, UserMatchLike.ActionType.LIKE));
    }

    @Test
    @DisplayName("标记匹配成功")
    void markAsMatchedSuccessfully() {
        // Given
        UserMatchLike matchLike = UserMatchLike.swipe(1L, 100L, 200L, UserMatchLike.ActionType.LIKE);

        // When
        matchLike.markAsMatched();

        // Then
        assertNotNull(matchLike.getMatchTime());
    }
}
