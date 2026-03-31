package com.quadra.system.domain.model;

import com.quadra.system.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysRole 聚合根单元测试
 */
@DisplayName("SysRole 聚合根测试")
class SysRoleTest {

    @Test
    @DisplayName("创建角色成功")
    void createRoleSuccessfully() {
        // Given
        Long id = 1L;
        String roleCode = "ADMIN";
        String roleName = "管理员";
        String description = "系统管理员";

        // When
        SysRole role = SysRole.create(id, roleCode, roleName, description);

        // Then
        assertNotNull(role);
        assertEquals(roleCode, role.getRoleCode());
        assertEquals(roleName, role.getRoleName());
        assertEquals(1, role.getStatus());
    }

    @Test
    @DisplayName("创建角色 - 角色编码为空应抛出异常")
    void createRoleWithEmptyRoleCode() {
        // Then
        assertThrows(DomainException.class, 
            () -> SysRole.create(1L, "", "name", "desc"));
    }

    @Test
    @DisplayName("授予菜单成功")
    void grantMenusSuccessfully() {
        // Given
        SysRole role = SysRole.create(1L, "ADMIN", "管理员", "desc");

        // When
        role.grantMenus(java.util.List.of(1L, 2L, 3L));

        // Then
        assertEquals(3, role.getMenuIds().size());
    }
}
