package com.quadra.system.adapter.in.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.system.adapter.in.web.common.Result;
import com.quadra.system.adapter.in.web.common.ResultCode;
import com.quadra.system.adapter.in.web.context.AdminContext;
import com.quadra.system.application.port.in.ParseAdminTokenUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理端认证拦截器
 * 与用户端隔离，使用独立的 JWT Scope
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final ParseAdminTokenUseCase parseAdminTokenUseCase;
    private final ObjectMapper objectMapper;

    public AdminAuthInterceptor(ParseAdminTokenUseCase parseAdminTokenUseCase, ObjectMapper objectMapper) {
        this.parseAdminTokenUseCase = parseAdminTokenUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Long adminId = parseAdminTokenUseCase.parseAdminId(token);
                if (adminId != null) {
                    AdminContext.setAdminId(adminId);
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
        AdminContext.clear();
    }
}
