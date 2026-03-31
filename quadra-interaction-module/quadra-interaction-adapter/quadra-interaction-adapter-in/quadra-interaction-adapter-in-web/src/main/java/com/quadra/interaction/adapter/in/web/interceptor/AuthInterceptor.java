package com.quadra.interaction.adapter.in.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.interaction.adapter.in.web.common.Result;
import com.quadra.interaction.adapter.in.web.common.ResultCode;
import com.quadra.interaction.adapter.in.web.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 从请求头中解析用户ID
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取用户ID（实际项目应从 JWT 解析）
        String userIdHeader = request.getHeader("X-User-Id");
        
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                UserContext.setUserId(userId);
                return true;
            } catch (NumberFormatException ignored) {
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.failure(ResultCode.UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
