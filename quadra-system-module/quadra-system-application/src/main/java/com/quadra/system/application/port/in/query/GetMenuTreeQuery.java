package com.quadra.system.application.port.in.query;

import com.quadra.system.application.port.in.dto.MenuTreeDTO;
import java.util.List;

public interface GetMenuTreeQuery {
    List<MenuTreeDTO> getMenuTree();
    List<MenuTreeDTO> getMenuTreeByAdminId(Long adminId);
}
