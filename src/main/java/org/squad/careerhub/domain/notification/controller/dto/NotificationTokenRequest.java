package org.squad.careerhub.domain.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Schema(description = "FCM 토큰 등록 요청 DTO")
@Builder
public record NotificationTokenRequest(

    @Schema(description = "FCM 토큰", example = "fcm-token-xxx-yyy-zzz")
    @NotBlank(message = "FCM 토큰은 필수 값입니다.")
    String fcmToken,

    @Schema(description = "디바이스 ID", example = "device-uuid-1234")
    @NotBlank(message = "디바이스 ID는 필수 값입니다.")
    String deviceId,

    @Schema(
        description = "디바이스 유형",
        example = "WEB",
        allowableValues = {"WEB", "ANDROID", "IOS"}
    )
    @NotBlank(message = "디바이스 유형은 필수 값입니다.")
    String deviceType,

    @Schema(description = "OS 버전", example = "iOS 18.1")
    @NotNull(message = "OS 버전은 필수 값입니다.")
    String osVersion,

    @Schema(description = "앱 버전", example = "1.0.0")
    @NotNull(message = "앱 버전은 필수 값입니다.")
    String appVersion
) {

    public NotificationTokenParamRequest toParam(Long memberId) {
        return NotificationTokenParamRequest.of(
            memberId,
            fcmToken,
            deviceId,
            deviceType,
            osVersion,
            appVersion
        );
    }

    public static NotificationTokenRequest mock() {
        return NotificationTokenRequest.builder()
            .fcmToken("fcm-token-xxx-yyy-zzz")
            .deviceId("device-uuid-1234")
            .deviceType("WEB")
            .osVersion("macOS 15.0")
            .appVersion("1.0.0")
            .build();
    }
}