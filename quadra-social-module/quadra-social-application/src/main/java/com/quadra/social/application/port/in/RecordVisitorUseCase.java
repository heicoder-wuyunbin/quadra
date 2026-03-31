package com.quadra.social.application.port.in;

import com.quadra.social.application.port.in.command.RecordVisitorCommand;

public interface RecordVisitorUseCase {
    /**
     * 记录访客
     * @param command 记录访客指令
     */
    void recordVisitor(RecordVisitorCommand command);
}
