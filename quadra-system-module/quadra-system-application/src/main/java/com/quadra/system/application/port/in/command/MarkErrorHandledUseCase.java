package com.quadra.system.application.port.in.command;

public interface MarkErrorHandledUseCase {
    void markHandled(String id, Long adminId, String adminName);
}
