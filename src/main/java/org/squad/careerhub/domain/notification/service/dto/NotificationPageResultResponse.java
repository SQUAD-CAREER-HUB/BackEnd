package org.squad.careerhub.domain.notification.service.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record NotificationPageResultResponse(
    List<NotificationResultResponse> notifications,
    boolean hasNext,
    Long nextCursorId
) {
    public static NotificationPageResultResponse of(
        List<NotificationResultResponse> notifications,
        boolean hasNext,
        Long nextCursorId
    ) {
        return NotificationPageResultResponse.builder()
            .notifications(notifications)
            .hasNext(hasNext)
            .nextCursorId(nextCursorId)
            .build();
    }
}
