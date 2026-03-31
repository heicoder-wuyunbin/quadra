package com.quadra.system.adapter.in.web.context;

/**
 * 管理端上下文
 * 与用户端隔离，存储管理员ID
 */
public class AdminContext {
    private static final ThreadLocal<Long> ADMIN_ID = new ThreadLocal<>();

    public static void setAdminId(Long adminId) {
        ADMIN_ID.set(adminId);
    }

    public static Long getAdminId() {
        return ADMIN_ID.get();
    }

    public static void clear() {
        ADMIN_ID.remove();
    }
}
