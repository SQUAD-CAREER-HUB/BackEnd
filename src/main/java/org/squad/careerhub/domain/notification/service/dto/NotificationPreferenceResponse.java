package org.squad.careerhub.domain.notification.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.notification.entity.NotificationPreference;
import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;

@Schema(description = "알림 수신 설정 응답 DTO (이벤트별/플랫폼별)")
@Builder
public record NotificationPreferenceResponse(

        @Schema(
                description = "알림 플랫폼",
                example = "WEB",
                allowableValues = {"WEB", "ANDROID", "IOS"}
        )
        NotificationPlatform platform,

        @Schema(
                description = "알림 이벤트",
                example = "INTERVIEW_D1"
        )
        NotificationEvent event,

        @Schema(
                description = "해당 이벤트 알림 수신 여부",
                example = "true"
        )
        boolean enabled,

        @Schema(
                description = """
                        설정 생성 시각.
                        - DB에 설정 row가 존재하는 경우에만 값이 존재합니다.
                        - 기본값(default)로 계산되어 내려간 경우 null 입니다.
                        """,
                example = "2026-01-10T00:12:34",
                nullable = true
        )
        LocalDateTime createdAt,

        @Schema(
                description = """
                        설정 수정 시각.
                        - DB에 설정 row가 존재하는 경우에만 값이 존재합니다.
                        - 기본값(default)로 계산되어 내려간 경우 null 입니다.
                        """,
                example = "2026-01-10T00:15:10",
                nullable = true
        )
        LocalDateTime updatedAt
) {

    public static NotificationPreferenceResponse from(NotificationPreference pref) {
        return NotificationPreferenceResponse.builder()
                .platform(pref.getPlatform())
                .event(pref.getEvent())
                .enabled(pref.isEnabled())
                .createdAt(pref.getCreatedAt())
                .updatedAt(pref.getUpdatedAt())
                .build();
    }

    public static NotificationPreferenceResponse of(
            NotificationEvent event,
            NotificationPlatform platform,
            boolean enabled
    ) {
        return NotificationPreferenceResponse.builder()
                .event(event)
                .platform(platform)
                .enabled(enabled)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }
}
