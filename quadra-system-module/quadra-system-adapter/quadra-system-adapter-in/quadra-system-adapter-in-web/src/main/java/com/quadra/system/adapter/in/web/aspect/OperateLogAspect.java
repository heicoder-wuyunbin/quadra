package com.quadra.system.adapter.in.web.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.system.adapter.in.web.context.AdminContext;
import com.quadra.system.application.port.in.SaveOperationLogUseCase;
import com.quadra.system.application.port.in.command.SaveOperationLogCommand;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class OperateLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperateLogAspect.class);

    private final SaveOperationLogUseCase saveOperationLogUseCase;
    private final ObjectMapper objectMapper;

    public OperateLogAspect(SaveOperationLogUseCase saveOperationLogUseCase, ObjectMapper objectMapper) {
        this.saveOperationLogUseCase = saveOperationLogUseCase;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Integer responseStatus = 200;
        Object result = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            responseStatus = 500;
            throw e;
        } finally {
            long executeTime = System.currentTimeMillis() - startTime;
            
            HttpServletRequest request = null;
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
            }

            Long adminId = AdminContext.getAdminId();
            String adminName = "Admin_" + (adminId != null ? adminId : "Unknown");

            String ipAddress = request != null ? request.getRemoteAddr() : "";
            String userAgent = request != null ? request.getHeader("User-Agent") : "";
            
            String tempRequestParams = "";
            try {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    tempRequestParams = objectMapper.writeValueAsString(args);
                }
            } catch (Exception ignored) {
            }
            final String requestParams = tempRequestParams;

            String module = logOperation.module();
            String action = logOperation.action();
            Integer finalResponseStatus = responseStatus;
            
            CompletableFuture.runAsync(() -> {
                try {
                    SaveOperationLogCommand command = new SaveOperationLogCommand(
                            adminId,
                            adminName,
                            module,
                            action,
                            null, // targetId
                            ipAddress,
                            userAgent,
                            requestParams,
                            finalResponseStatus,
                            (int) executeTime
                    );
                    saveOperationLogUseCase.save(command);
                } catch (Exception e) {
                    log.error("Failed to save operation log", e);
                }
            });
        }
    }
}
