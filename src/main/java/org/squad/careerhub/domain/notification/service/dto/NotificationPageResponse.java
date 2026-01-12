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

    public static NotificationPageResponse from(List<NotificationResponse> items, boolean hasNext) {
        Long nextCursorId = (hasNext && !items.isEmpty())
                ? items.get(items.size() - 1).notificationId()
                : null;

        return NotificationPageResponse.builder()
                .notifications(items)
                .hasNext(hasNext)
                .nextCursorId(nextCursorId)
                .build();
    }
}