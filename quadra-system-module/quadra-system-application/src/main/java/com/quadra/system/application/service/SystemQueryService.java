package com.quadra.system.application.service;

import com.quadra.system.application.port.in.dto.AdminDTO;
import com.quadra.system.application.port.in.dto.DailyAnalysisDTO;
import com.quadra.system.application.port.in.dto.MenuTreeDTO;
import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.UserAdminDTO;
import com.quadra.system.application.port.in.dto.UserDetailDTO;
import com.quadra.system.application.port.in.query.GetDailyAnalysisQuery;
import com.quadra.system.application.port.in.query.GetMenuTreeQuery;
import com.quadra.system.application.port.in.query.ListAdminsQuery;
import com.quadra.system.application.port.in.query.ListUsersQuery;
import com.quadra.system.application.port.out.AdminQueryPort;
import com.quadra.system.application.port.out.AnalysisQueryPort;
import com.quadra.system.application.port.out.MenuQueryPort;
import com.quadra.system.application.port.out.UserQueryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SystemQueryService implements ListAdminsQuery, ListUsersQuery, GetMenuTreeQuery, GetDailyAnalysisQuery {

    private final AdminQueryPort adminQueryPort;
    private final MenuQueryPort menuQueryPort;
    private final AnalysisQueryPort analysisQueryPort;
    private final UserQueryPort userQueryPort;

    public SystemQueryService(
            AdminQueryPort adminQueryPort,
            MenuQueryPort menuQueryPort,
            AnalysisQueryPort analysisQueryPort,
            UserQueryPort userQueryPort) {
        this.adminQueryPort = adminQueryPort;
        this.menuQueryPort = menuQueryPort;
        this.analysisQueryPort = analysisQueryPort;
        this.userQueryPort = userQueryPort;
    }

    @Override
    public PageResult<AdminDTO> listAdmins(Integer status, int page, int size) {
        return adminQueryPort.findAdmins(status, page, size);
    }

    @Override
    public AdminDTO getAdminById(Long id) {
        return adminQueryPort.findAdminById(id);
    }

    @Override
    public PageResult<UserAdminDTO> listUsers(String mobile, Integer status, int page, int size) {
        return userQueryPort.findUsers(mobile, status, page, size);
    }

    @Override
    public UserDetailDTO getUserDetailById(String id) {
        return userQueryPort.findUserDetailById(id);
    }

    @Override
    public List<MenuTreeDTO> getMenuTree() {
        return menuQueryPort.findAllMenus();
    }

    @Override
    public List<MenuTreeDTO> getMenuTreeByAdminId(Long adminId) {
        return menuQueryPort.findMenusByAdminId(adminId);
    }

    @Override
    public DailyAnalysisDTO getByDate(LocalDate date) {
        return analysisQueryPort.findByDate(date);
    }

    @Override
    public List<DailyAnalysisDTO> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return analysisQueryPort.findByDateRange(startDate, endDate);
    }
}
