package org.squad.careerhub.domain.notification.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResultResponse(
    Long notificationId,
    String type,
    String title,
    String message,
    String targetType,
    Long targetId,
    boolean read,
    LocalDateTime createdAt
) {
}
