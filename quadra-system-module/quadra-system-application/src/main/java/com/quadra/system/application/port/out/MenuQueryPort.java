package com.quadra.system.application.port.out;

import com.quadra.system.application.port.in.dto.MenuTreeDTO;
import java.util.List;

public interface MenuQueryPort {
    List<MenuTreeDTO> findAllMenus();
    List<MenuTreeDTO> findMenusByAdminId(Long adminId);
}
