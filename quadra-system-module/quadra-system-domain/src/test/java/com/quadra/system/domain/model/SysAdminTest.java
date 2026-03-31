package com.quadra.system.domain.model;

import com.quadra.system.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysAdmin 聚合根单元测试
 */
@DisplayName("SysAdmin 聚合根测试")
class SysAdminTest {

    @Test
    @DisplayName("创建管理员成功")
    void createAdminSuccessfully() {
        // Given
        Long id = 1L;
        String username = "admin";
        String password = "encrypted_password";
        String realName = "管理员";

        // When
        SysAdmin admin = SysAdmin.create(id, username, password, realName);

        // Then
        assertNotNull(admin);
        assertEquals(id, admin.getId());
        assertEquals(username, admin.getUsername());
        assertEquals(1, admin.getStatus());
        assertEquals(0, admin.getDeleted());
        assertFalse(admin.getDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("创建管理员 - 用户名为空应抛出异常")
    void createAdminWithEmptyUsername() {
        // Then
        assertThrows(DomainException.class, 
            () -> SysAdmin.create(1L, "", "pwd", "name"));
    }

    @Test
    @DisplayName("启用管理员成功")
    void enableAdminSuccessfully() {
        // Given
        SysAdmin admin = SysAdmin.create(1L, "admin", "pwd", "name");
        admin.disable();

        // When
        admin.enable();

        // Then
        assertEquals(1, admin.getStatus());
    }

    @Test
    @DisplayName("禁用管理员成功")
    void disableAdminSuccessfully() {
        // Given
        SysAdmin admin = SysAdmin.create(1L, "admin", "pwd", "name");

        // When
        admin.disable();

        // Then
        assertEquals(0, admin.getStatus());
    }

    @Test
    @DisplayName("分配角色成功")
    void assignRolesSuccessfully() {
        // Given
        SysAdmin admin = SysAdmin.create(1L, "admin", "pwd", "name");
        List<Long> roleIds = List.of(1L, 2L);

        // When
        admin.assignRoles(roleIds);

        // Then
        assertEquals(2, admin.getRoleIds().size());
    }
}
