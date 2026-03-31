package com.quadra.system.application.service;

import com.quadra.system.application.port.in.command.AssignRoleCommand;
import com.quadra.system.application.port.in.command.CreateAdminCommand;
import com.quadra.system.application.port.in.command.CreateRoleCommand;
import com.quadra.system.application.port.out.AdminRepositoryPort;
import com.quadra.system.application.port.out.RoleRepositoryPort;
import com.quadra.system.application.port.out.MenuRepositoryPort;
import com.quadra.system.application.port.out.AnalysisRepositoryPort;
import com.quadra.system.application.port.out.EventPublisherPort;
import com.quadra.system.application.port.out.UserCommandPort;
import com.quadra.system.domain.exception.DomainException;
import com.quadra.system.domain.model.SysAdmin;
import com.quadra.system.domain.model.SysRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SystemApplicationService 单元测试
 */
@DisplayName("SystemApplicationService 测试")
class SystemApplicationServiceTest {

    @Mock
    private AdminRepositoryPort adminRepositoryPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private MenuRepositoryPort menuRepositoryPort;

    @Mock
    private AnalysisRepositoryPort analysisRepositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private UserCommandPort userCommandPort;

    private SystemApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SystemApplicationService(
            adminRepositoryPort,
            roleRepositoryPort,
            menuRepositoryPort,
            analysisRepositoryPort,
            eventPublisherPort,
            userCommandPort
        );
    }

    @Test
    @DisplayName("创建管理员成功")
    void createAdminSuccessfully() {
        // Given
        Long adminId = 1L;
        String username = "admin";
        String password = "encrypted_pwd";
        String realName = "管理员";

        CreateAdminCommand command = new CreateAdminCommand(username, password, realName);

        when(adminRepositoryPort.findByUsername(username)).thenReturn(null);
        when(adminRepositoryPort.nextId()).thenReturn(adminId);
        doNothing().when(adminRepositoryPort).save(any(SysAdmin.class));

        // When
        Long result = service.createAdmin(command);

        // Then
        assertEquals(adminId, result);
        verify(adminRepositoryPort).save(any(SysAdmin.class));
        verify(eventPublisherPort).publish(any());
    }

    @Test
    @DisplayName("创建管理员 - 用户名已存在应抛出异常")
    void createAdminWithExistingUsername() {
        // Given
        String username = "admin";
        CreateAdminCommand command = new CreateAdminCommand(username, "pwd", "name");
        SysAdmin existingAdmin = SysAdmin.create(999L, username, "pwd", "name");

        when(adminRepositoryPort.findByUsername(username)).thenReturn(existingAdmin);

        // Then
        assertThrows(DomainException.class, () -> service.createAdmin(command));
    }

    @Test
    @DisplayName("创建角色成功")
    void createRoleSuccessfully() {
        // Given
        Long roleId = 1L;
        String roleCode = "ADMIN";
        String roleName = "管理员";
        String description = "系统管理员";

        CreateRoleCommand command = new CreateRoleCommand(roleCode, roleName, description);

        when(roleRepositoryPort.findByRoleCode(roleCode)).thenReturn(null);
        when(roleRepositoryPort.nextId()).thenReturn(roleId);
        doNothing().when(roleRepositoryPort).save(any(SysRole.class));

        // When
        Long result = service.createRole(command);

        // Then
        assertEquals(roleId, result);
        verify(roleRepositoryPort).save(any(SysRole.class));
    }

    @Test
    @DisplayName("分配角色成功")
    void assignRoleSuccessfully() {
        // Given
        Long adminId = 1L;
        List<Long> roleIds = List.of(2L, 3L);
        SysAdmin admin = SysAdmin.create(adminId, "admin", "pwd", "name");

        AssignRoleCommand command = new AssignRoleCommand(adminId, roleIds);

        when(adminRepositoryPort.findById(adminId)).thenReturn(admin);
        doNothing().when(adminRepositoryPort).deleteAdminRoleRelation(adminId);
        doNothing().when(adminRepositoryPort).insertAdminRoleRelation(adminId, roleIds);

        // When
        service.assignRole(command);

        // Then
        verify(adminRepositoryPort).deleteAdminRoleRelation(adminId);
        verify(adminRepositoryPort).insertAdminRoleRelation(adminId, roleIds);
    }

    @Test
    @DisplayName("生成日报成功")
    void generateDailyAnalysisSuccessfully() {
        // Given
        LocalDate date = LocalDate.now();
        Long analysisId = 1L;

        when(analysisRepositoryPort.findByRecordDate(date)).thenReturn(null);
        when(analysisRepositoryPort.nextId()).thenReturn(analysisId);
        doNothing().when(analysisRepositoryPort).save(any());

        // When
        service.generateDailyAnalysis(date);

        // Then
        verify(analysisRepositoryPort).save(any());
    }
}
