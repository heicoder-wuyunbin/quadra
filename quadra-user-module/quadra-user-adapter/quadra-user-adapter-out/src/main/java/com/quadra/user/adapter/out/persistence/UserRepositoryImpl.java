package com.quadra.user.adapter.out.persistence;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.user.adapter.out.persistence.entity.UserDO;
import com.quadra.user.adapter.out.persistence.entity.UserProfileDO;
import com.quadra.user.adapter.out.persistence.entity.UserSettingDO;
import com.quadra.user.adapter.out.persistence.mapper.UserMapper;
import com.quadra.user.adapter.out.persistence.mapper.UserProfileMapper;
import com.quadra.user.adapter.out.persistence.mapper.UserSettingMapper;
import com.quadra.user.application.port.out.UserRepositoryPort;
import com.quadra.user.domain.model.User;
import com.quadra.user.domain.exception.DomainException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepositoryImpl implements UserRepositoryPort {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final UserSettingMapper userSettingMapper;

    public UserRepositoryImpl(UserMapper userMapper, UserProfileMapper userProfileMapper, UserSettingMapper userSettingMapper) {
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.userSettingMapper = userSettingMapper;
    }

    @Override
    @Transactional
    public void save(User user) {
        // 将 Domain 对象转换为 DO 对象
        UserDO userDO = new UserDO();
        userDO.setId(user.getId());
        userDO.setMobile(user.getMobile());
        userDO.setPassword(user.getPassword());
        userDO.setStatus(user.getStatus());
        userDO.setDeleted(user.getDeleted());

        UserProfileDO profileDO = new UserProfileDO();
        profileDO.setId(user.getProfile().getId());
        profileDO.setGender(user.getProfile().getGender());
        profileDO.setMarriage(user.getProfile().getMarriage());
        profileDO.setTags(user.getProfile().getTags());

        UserSettingDO settingDO = new UserSettingDO();
        settingDO.setId(user.getSetting().getId());
        settingDO.setLikeNotification(user.getSetting().getLikeNotification());
        settingDO.setCommentNotification(user.getSetting().getCommentNotification());
        settingDO.setSystemNotification(user.getSetting().getSystemNotification());

        try {
            // 同一个本地事务中分别插入三张表
            userMapper.insert(userDO);
            userProfileMapper.insert(profileDO);
            userSettingMapper.insert(settingDO);
        } catch (DuplicateKeyException e) {
            throw new DomainException("手机号已存在，注册失败", e);
        }
    }

    @Override
    public User findById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO == null) {
            return null;
        }
        
        // 查询出相关的实体
        UserProfileDO profileDO = userProfileMapper.selectById(id);
        UserSettingDO settingDO = userSettingMapper.selectById(id);

        return assembleUser(userDO, profileDO, settingDO);
    }

    @Override
    @Transactional
    public void update(User user) {
        // 主要是更新 UserProfile
        if (user.getProfile() != null) {
            UserProfileDO profileDO = new UserProfileDO();
            profileDO.setId(user.getProfile().getId());
            profileDO.setNickname(user.getProfile().getNickname());
            profileDO.setGender(user.getProfile().getGender());
            profileDO.setBirthday(user.getProfile().getBirthday());
            profileDO.setCity(user.getProfile().getCity());
            profileDO.setAvatar(user.getProfile().getAvatar());
            profileDO.setIncome(user.getProfile().getIncome());
            profileDO.setProfession(user.getProfile().getProfession());
            profileDO.setMarriage(user.getProfile().getMarriage());
            profileDO.setCoverPic(user.getProfile().getCoverPic());
            profileDO.setTags(user.getProfile().getTags());
            
            userProfileMapper.updateById(profileDO);
        }

        // 更新 UserSetting
        if (user.getSetting() != null) {
            UserSettingDO settingDO = new UserSettingDO();
            settingDO.setId(user.getSetting().getId());
            settingDO.setLikeNotification(user.getSetting().getLikeNotification());
            settingDO.setCommentNotification(user.getSetting().getCommentNotification());
            settingDO.setSystemNotification(user.getSetting().getSystemNotification());
            
            userSettingMapper.updateById(settingDO);
        }
        
        // 根据需要，如果 User 本身发生变化，也可以在此更新
        UserDO userDO = new UserDO();
        userDO.setId(user.getId());
        userDO.setStatus(user.getStatus());
        userMapper.updateById(userDO);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    private User assembleUser(UserDO userDO, UserProfileDO profileDO, UserSettingDO settingDO) {
        try {
            java.lang.reflect.Constructor<User> constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();
            
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userDO.getId());

            java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(user, userDO.getPassword());

            java.lang.reflect.Field statusField = User.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(user, userDO.getStatus());

            if (profileDO != null) {
                java.lang.reflect.Field profileField = User.class.getDeclaredField("profile");
                profileField.setAccessible(true);
                
                com.quadra.user.domain.model.UserProfile profile = new com.quadra.user.domain.model.UserProfile(userDO.getId());
                profile.updateBaseInfo(
                    profileDO.getNickname(),
                    profileDO.getGender(),
                    profileDO.getBirthday(),
                    profileDO.getCity(),
                    profileDO.getAvatar(),
                    profileDO.getIncome(),
                    profileDO.getProfession(),
                    profileDO.getMarriage(),
                    profileDO.getCoverPic(),
                    profileDO.getTags()
                );
                profileField.set(user, profile);
            }

            if (settingDO != null) {
                java.lang.reflect.Field settingField = User.class.getDeclaredField("setting");
                settingField.setAccessible(true);

                com.quadra.user.domain.model.UserSetting setting = new com.quadra.user.domain.model.UserSetting(userDO.getId());
                setting.updateNotification(
                    settingDO.getLikeNotification(),
                    settingDO.getCommentNotification(),
                    settingDO.getSystemNotification()
                );
                settingField.set(user, setting);
            }

            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore User from DB", e);
        }
    }
}
