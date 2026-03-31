package com.quadra.social.application.service;

import com.quadra.social.application.port.in.command.FollowUserCommand;
import com.quadra.social.application.port.in.command.UnfollowUserCommand;
import com.quadra.social.application.port.out.FollowRepositoryPort;
import com.quadra.social.domain.exception.DomainException;
import com.quadra.social.domain.model.UserFollow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SocialApplicationService 单元测试
 */
@DisplayName("SocialApplicationService 测试")
class SocialApplicationServiceTest {

    @Mock
    private FollowRepositoryPort followRepositoryPort;

    private SocialApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SocialApplicationService(followRepositoryPort);
    }

    @Test
    @DisplayName("关注用户成功")
    void followUserSuccessfully() {
        // Given
        Long userId = 100L;
        Long targetUserId = 200L;
        Long followId = 1L;

        FollowUserCommand command = new FollowUserCommand(userId, targetUserId);

        when(followRepositoryPort.findByUserIdAndTargetUserId(userId, targetUserId)).thenReturn(null);
        when(followRepositoryPort.nextId()).thenReturn(followId);
        doNothing().when(followRepositoryPort).save(any(UserFollow.class));

        // When
        service.follow(command);

        // Then
        verify(followRepositoryPort).save(any(UserFollow.class));
    }

    @Test
    @DisplayName("重新关注已取消关注的用户")
    void refollowPreviouslyUnfollowedUser() {
        // Given
        Long userId = 100L;
        Long targetUserId = 200L;
        UserFollow existingFollow = UserFollow.follow(1L, userId, targetUserId);
        existingFollow.unfollow();

        FollowUserCommand command = new FollowUserCommand(userId, targetUserId);

        when(followRepositoryPort.findByUserIdAndTargetUserId(userId, targetUserId)).thenReturn(existingFollow);
        doNothing().when(followRepositoryPort).update(any(UserFollow.class));

        // When
        service.follow(command);

        // Then
        verify(followRepositoryPort).update(any(UserFollow.class));
        // 注意：由于 service 内部创建了一个新的 UserFollow 对象并更新，
        // 所以 existingFollow 的 deleted 状态仍然是 1
        // 这里主要验证 update 方法被调用
    }

    @Test
    @DisplayName("重复关注应抛出异常")
    void followAlreadyFollowedUser() {
        // Given
        Long userId = 100L;
        Long targetUserId = 200L;
        UserFollow existingFollow = UserFollow.follow(1L, userId, targetUserId);

        FollowUserCommand command = new FollowUserCommand(userId, targetUserId);

        when(followRepositoryPort.findByUserIdAndTargetUserId(userId, targetUserId)).thenReturn(existingFollow);

        // Then
        assertThrows(DomainException.class, () -> service.follow(command));
    }

    @Test
    @DisplayName("取消关注成功")
    void unfollowSuccessfully() {
        // Given
        Long userId = 100L;
        Long targetUserId = 200L;
        UserFollow userFollow = UserFollow.follow(1L, userId, targetUserId);

        UnfollowUserCommand command = new UnfollowUserCommand(userId, targetUserId);

        when(followRepositoryPort.findByUserIdAndTargetUserId(userId, targetUserId)).thenReturn(userFollow);

        // When
        service.unfollow(command);

        // Then
        verify(followRepositoryPort).update(userFollow);
        assertEquals(1, userFollow.getDeleted());
    }

    @Test
    @DisplayName("取消关注 - 未关注应抛出异常")
    void unfollowNotFollowedUser() {
        // Given
        Long userId = 100L;
        Long targetUserId = 200L;
        UnfollowUserCommand command = new UnfollowUserCommand(userId, targetUserId);

        when(followRepositoryPort.findByUserIdAndTargetUserId(userId, targetUserId)).thenReturn(null);

        // Then
        assertThrows(DomainException.class, () -> service.unfollow(command));
    }
}
