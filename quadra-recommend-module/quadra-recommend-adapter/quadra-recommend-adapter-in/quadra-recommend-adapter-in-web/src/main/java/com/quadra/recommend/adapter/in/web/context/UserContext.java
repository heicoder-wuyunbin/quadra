package com.quadra.recommend.adapter.in.web.context;

/**
 * 用户上下文
 * 用于在请求线程中存储当前登录用户信息
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}
