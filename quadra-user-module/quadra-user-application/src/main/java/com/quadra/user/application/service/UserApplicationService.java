package com.quadra.user.application.service;

import com.quadra.user.application.port.in.RegisterUserUseCase;
import com.quadra.user.application.port.in.UpdateProfileUseCase;
import com.quadra.user.application.port.in.UpdateSettingUseCase;
import com.quadra.user.application.port.in.command.RegisterUserCommand;
import com.quadra.user.application.port.in.command.UpdateProfileCommand;
import com.quadra.user.application.port.in.command.UpdateSettingCommand;
import com.quadra.user.application.port.out.EventPublisherPort;
import com.quadra.user.application.port.out.PasswordEncoderPort;
import com.quadra.user.application.port.out.UserRepositoryPort;
import com.quadra.user.domain.exception.DomainException;
import com.quadra.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService implements RegisterUserUseCase, UpdateProfileUseCase, UpdateSettingUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserApplicationService(
            UserRepositoryPort userRepositoryPort,
            EventPublisherPort eventPublisherPort,
            PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public Long register(RegisterUserCommand command) {
        // 1. 获取全局唯一ID
        Long userId = userRepositoryPort.nextId();
        
        // 2. 密码加密 (Application 层负责)
        String encryptedPassword = passwordEncoderPort.encode(command.rawPassword());

        // 3. Domain 聚合根创建与校验，产生初始事件
        User user = User.register(userId, command.mobile(), encryptedPassword);

        // 4. 持久化聚合根 (Adapter 层需要处理拆表与同事务提交)
        userRepositoryPort.save(user);

        // 5. 提取并持久化领域事件到 Outbox 表 (同事务)
        if (!user.getDomainEvents().isEmpty()) {
            eventPublisherPort.publish(user.getDomainEvents());
            user.clearDomainEvents();
        }

        return userId;
    }

    @Override
    public void updateProfile(UpdateProfileCommand command) {
        // 1. 根据 ID 获取聚合根
        User user = userRepositoryPort.findById(command.userId());
        if (user == null) {
            throw new DomainException("用户不存在");
        }

        // 2. 委托给领域聚合根处理业务逻辑（这里只有赋值和状态检查）
        user.updateProfile(
            command.nickname(),
            command.gender(),
            command.birthday(),
            command.city(),
            command.avatar(),
            command.income(),
            command.profession(),
            command.marriage(),
            command.coverPic(),
            command.tags()
        );

        // 3. 持久化更新
        userRepositoryPort.update(user);
    }

    @Override
    public void updateSetting(UpdateSettingCommand command) {
        User user = userRepositoryPort.findById(command.userId());
        if (user == null) {
            throw new DomainException("用户不存在");
        }

        user.updateSetting(
            command.likeNotification(),
            command.commentNotification(),
            command.systemNotification()
        );

        userRepositoryPort.update(user);
    }
}
