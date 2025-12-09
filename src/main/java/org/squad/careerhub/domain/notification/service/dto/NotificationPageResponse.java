package org.squad.careerhub.domain.notification.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "알림 목록 페이지 응답 DTO (커서 기반 페이지네이션)")
@Builder
public record NotificationPageResponse(

    @Schema(description = "알림 목록")
    List<NotificationResponse> notifications,

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    boolean hasNext,

    @Schema(description = "다음 페이지 조회용 커서 ID", example = "100")
    Long nextCursorId
) {


    public static NotificationPageResponse mock() {
        NotificationResponse n1 = NotificationResponse.mock();

        NotificationResponse n2 = NotificationResponse.builder()
            .notificationId(100L)
            .type("DEADLINE_REMINDER")
            .title("[D-3] 카카오 백엔드 지원 마감 예정입니다.")
            .message("2025-12-03 23:59, 카카오 백엔드 공고 마감 예정입니다.")
            .targetType("APPLICATION")
            .targetId(5L)
            .read(true)
            .createdAt(n1.createdAt().minusHours(3))
            .build();

        return NotificationPageResponse.builder()
            .notifications(List.of(n1, n2))
            .hasNext(true)
            .nextCursorId(100L)
            .build();
    }
}