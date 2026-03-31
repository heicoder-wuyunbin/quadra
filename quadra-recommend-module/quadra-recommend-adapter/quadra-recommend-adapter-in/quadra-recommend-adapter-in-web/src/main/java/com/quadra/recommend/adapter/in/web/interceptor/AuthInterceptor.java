package com.quadra.recommend.adapter.in.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.recommend.adapter.in.web.common.Result;
import com.quadra.recommend.adapter.in.web.common.ResultCode;
import com.quadra.recommend.adapter.in.web.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 简化版本：从请求头获取用户ID
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取用户ID（简化实现，实际应该解析 JWT Token）
        String userIdHeader = request.getHeader("X-User-Id");
        
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                UserContext.setUserId(userId);
                return true;
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }

        // 对于推荐服务，允许匿名访问，但需要用户ID
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
