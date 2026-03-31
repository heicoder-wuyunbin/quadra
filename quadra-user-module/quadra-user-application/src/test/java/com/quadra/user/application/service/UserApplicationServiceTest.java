package com.quadra.user.application.service;

import com.quadra.user.application.port.in.command.RegisterUserCommand;
import com.quadra.user.application.port.in.command.UpdateProfileCommand;
import com.quadra.user.application.port.in.command.UpdateSettingCommand;
import com.quadra.user.application.port.out.EventPublisherPort;
import com.quadra.user.application.port.out.PasswordEncoderPort;
import com.quadra.user.application.port.out.UserRepositoryPort;
import com.quadra.user.domain.exception.DomainException;
import com.quadra.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * UserApplicationService 单元测试
 */
@DisplayName("UserApplicationService 测试")
class UserApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private UserApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserApplicationService(
            userRepositoryPort,
            eventPublisherPort,
            passwordEncoderPort
        );
    }

    @Test
    @DisplayName("注册用户成功")
    void registerUserSuccessfully() {
        // Given
        Long userId = 1L;
        String mobile = "13800138000";
        String rawPassword = "password123";
        String encryptedPassword = "bcrypt_encoded";

        RegisterUserCommand command = new RegisterUserCommand(mobile, rawPassword);

        when(userRepositoryPort.nextId()).thenReturn(userId);
        when(passwordEncoderPort.encode(rawPassword)).thenReturn(encryptedPassword);
        doNothing().when(userRepositoryPort).save(any(User.class));

        // When
        Long result = service.register(command);

        // Then
        assertEquals(userId, result);
        verify(userRepositoryPort).save(any(User.class));
        verify(eventPublisherPort).publish(any());
    }

    @Test
    @DisplayName("更新用户资料成功")
    void updateUserProfileSuccessfully() {
        // Given
        Long userId = 1L;
        String nickname = "新昵称";
        Integer gender = 1;

        UpdateProfileCommand command = new UpdateProfileCommand(
            userId, nickname, null, gender, null, null, null, null, null, null, null
        );

        User user = User.register(userId, "13800138000", "pwd");
        when(userRepositoryPort.findById(userId)).thenReturn(user);
        doNothing().when(userRepositoryPort).update(user);

        // When
        service.updateProfile(command);

        // Then
        verify(userRepositoryPort).update(user);
        assertNotNull(user.getProfile());
    }

    @Test
    @DisplayName("更新用户资料 - 用户不存在应抛出异常")
    void updateUserProfileNotFound() {
        // Given
        Long userId = 1L;
        UpdateProfileCommand command = new UpdateProfileCommand(
            userId, "昵称", null, 1, null, null, null, null, null, null, null
        );

        when(userRepositoryPort.findById(userId)).thenReturn(null);

        // Then
        assertThrows(DomainException.class, () -> service.updateProfile(command));
    }

    @Test
    @DisplayName("更新用户设置成功")
    void updateUserSettingSuccessfully() {
        // Given
        Long userId = 1L;
        Integer likeNotification = 1;
        Integer commentNotification = 0;
        Integer systemNotification = 1;

        UpdateSettingCommand command = new UpdateSettingCommand(
            userId, likeNotification, commentNotification, systemNotification
        );

        User user = User.register(userId, "13800138000", "pwd");
        when(userRepositoryPort.findById(userId)).thenReturn(user);
        doNothing().when(userRepositoryPort).update(user);

        // When
        service.updateSetting(command);

        // Then
        verify(userRepositoryPort).update(user);
    }
}
