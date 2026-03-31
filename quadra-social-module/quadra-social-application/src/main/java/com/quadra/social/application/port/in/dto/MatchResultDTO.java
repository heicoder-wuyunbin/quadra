package com.quadra.social.application.port.in.dto;

import java.time.LocalDateTime;

public record MatchResultDTO(
    boolean matched,
    Long matchId,
    Long targetUserId,
    LocalDateTime matchTime
) {
    public static MatchResultDTO notMatched() {
        return new MatchResultDTO(false, null, null, null);
    }
    
    public static MatchResultDTO matched(Long matchId, Long targetUserId, LocalDateTime matchTime) {
        return new MatchResultDTO(true, matchId, targetUserId, matchTime);
    }
}
