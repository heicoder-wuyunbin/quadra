package com.quadra.user.adapter.in.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.user.adapter.in.web.common.Result;
import com.quadra.user.adapter.in.web.common.ResultCode;
import com.quadra.user.adapter.in.web.context.UserContext;
import com.quadra.user.application.port.in.ParseAccessTokenUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ParseAccessTokenUseCase parseAccessTokenUseCase;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(ParseAccessTokenUseCase parseAccessTokenUseCase, ObjectMapper objectMapper) {
        this.parseAccessTokenUseCase = parseAccessTokenUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Long userId = parseAccessTokenUseCase.parseUserId(token);
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
}
