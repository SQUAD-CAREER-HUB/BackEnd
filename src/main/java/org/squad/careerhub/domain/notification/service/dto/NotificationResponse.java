package org.squad.careerhub.domain.notification.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.notification.enums.NotificationType;
import org.squad.careerhub.domain.notification.enums.TargetType;

@Schema(description = "알림 단건 응답 DTO")
@Builder
public record NotificationResponse(

    @Schema(description = "알림 ID", example = "101")
    Long notificationId,

    @Schema(
        description = "알림 유형",
        example = "INTERVIEW_REMINDER",
        allowableValues = {
            "INTERVIEW_REMINDER",
            "TASK_REMINDER",
            "DEADLINE_REMINDER",
            "SYSTEM",
            "COMMUNITY"
        }
    )
    NotificationType type,

    @Schema(description = "알림 제목", example = "[면접 D-1] 네이버 백엔드 면접이 내일 예정되어 있습니다.")
    String title,

    @Schema(
        description = "알림 내용",
        example = "2025-12-01 14:00, 네이버 백엔드 1차 면접 일정입니다."
    )
    String message,

    @Schema(
        description = "연결 리소스 유형",
        example = "INTERVIEW",
        allowableValues = {"APPLICATION","TASK", "INTERVIEW", "REVIEW", "OTHER"}
    )
    TargetType targetType,

    @Schema(description = "연결 리소스 ID", example = "10")
    Long targetId,

    @Schema(description = "읽음 여부", example = "false")
    boolean read,

    @Schema(description = "알림 생성 시각", example = "2025-11-30T21:00:00")
    LocalDateTime createdAt
) {

    public static NotificationResponse mock() {
        return NotificationResponse.builder()
            .notificationId(101L)
            .type(NotificationType.INTERVIEW_REMINDER)
            .title("[면접 D-1] 네이버 백엔드 1차 면접이 내일 예정되어 있습니다.")
            .message("2025-12-01 14:00, 네이버 백엔드 1차 면접 일정입니다.")
            .targetType(TargetType.INTERVIEW)
            .targetId(10L)
            .read(false)
            .createdAt(LocalDateTime.of(2025, 11, 30, 21, 0, 0))
            .build();
    }
}
