package org.squad.careerhub.domain.notification.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import javax.management.remote.NotificationResult;
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
//    public static NotificationResult from(Notification entity) {
//        return NotificationResult.builder()
//            .notificationId(entity.getId())
//            .type(entity.getType().name())
//            .title(entity.getTitle())
//            .message(entity.getMessage())
//            .targetType(entity.getTargetType().name())
//            .targetId(entity.getTargetId())
//            .read(entity.isRead())
//            .createdAt(entity.getCreatedAt())
//            .build();
//    }
}
