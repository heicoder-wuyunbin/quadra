package com.quadra.social.adapter.in.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.social.adapter.in.web.common.Result;
import com.quadra.social.adapter.in.web.common.ResultCode;
import com.quadra.social.adapter.in.web.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 注意：Social 模块依赖 User 模块的鉴权服务，这里简化实现
 * 实际生产环境中应该调用 User 模块的 Token 验证接口或使用共享的 JWT 验证逻辑
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // TODO: 实际生产环境应调用 User 模块验证 Token
                // 这里简化处理：假设 Token 格式为 "userId:{timestamp}"
                // 实际应使用 JWT 解析或调用远程服务
                Long userId = parseUserIdFromToken(token);
                if (userId != null) {
                    UserContext.setUserId(userId);
                    return true;
                }
            } catch (Exception ignored) {
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
    
    /**
     * 简化的 Token 解析，生产环境应使用 JWT 或调用 User 模块
     */
    private Long parseUserIdFromToken(String token) {
        // 这里仅作演示，实际应集成 JWT 验证
        // 假设前端传递的是一个模拟的 userId
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
