package com.quadra.system.adapter.in.web.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.system.application.port.in.SaveRequestLogUseCase;
import com.quadra.system.application.port.in.command.SaveRequestLogCommand;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理后台接口访问日志：
 * - 生成/透传 traceId（写入 MDC，Result.requestId 会复用）
 * - 记录 method/path/status/耗时/headers/body（限长）
 * - 写入数据库（sys_request_log）
 */
@Component
public class SysRequestLogFilter extends OncePerRequestFilter {

    private static final int BODY_MAX_LEN = 20_000;
    private static final String SERVICE_NAME = "quadra-system";

    private final SaveRequestLogUseCase saveRequestLogUseCase;
    private final ObjectMapper objectMapper;

    public SysRequestLogFilter(SaveRequestLogUseCase saveRequestLogUseCase, ObjectMapper objectMapper) {
        this.saveRequestLogUseCase = saveRequestLogUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/swagger-ui.html")
                || uri.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String traceId = getOrCreateTraceId(request);
        MDC.put("traceId", traceId);

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            try {
                int status = responseWrapper.getStatus();
                int durationMs = (int) (System.currentTimeMillis() - start);

                Long adminId = null;
                Object adminIdAttr = request.getAttribute("adminId");
                if (adminIdAttr instanceof Long) {
                    adminId = (Long) adminIdAttr;
                }

                String requestBody = readBody(requestWrapper.getContentAsByteArray());
                String responseBody = readBody(responseWrapper.getContentAsByteArray());
                String headersJson = safeToJson(maskHeaders(request));

                saveRequestLogUseCase.save(new SaveRequestLogCommand(
                        SERVICE_NAME,
                        traceId,
                        adminId,
                        request.getMethod(),
                        request.getRequestURI(),
                        request.getQueryString(),
                        status,
                        durationMs,
                        getClientIp(request),
                        request.getHeader("User-Agent"),
                        headersJson,
                        requestBody,
                        responseBody
                ));
            } catch (Exception ignored) {
                // 日志入库失败不影响主流程
            } finally {
                responseWrapper.copyBodyToResponse();
                MDC.remove("traceId");
            }
        }
    }

    private String getOrCreateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Request-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader("X-Trace-Id");
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = request.getParameter("requestId");
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        return traceId;
    }

    private String readBody(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        String str = new String(bytes, StandardCharsets.UTF_8);
        if (str.length() <= BODY_MAX_LEN) return str;
        return str.substring(0, BODY_MAX_LEN) + "...(truncated)";
    }

    private Map<String, Object> maskHeaders(HttpServletRequest request) {
        Map<String, Object> map = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            if (name == null) continue;
            String lower = name.toLowerCase();
            if (lower.equals("authorization") || lower.equals("cookie")) {
                map.put(name, "***");
                continue;
            }
            map.put(name, request.getHeader(name));
        }
        return map;
    }

    private String safeToJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // 可能是多个ip，取第一个
            int idx = xff.indexOf(',');
            return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
        }
        return request.getRemoteAddr();
    }
}
