package org.squad.careerhub.domain.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.service.dto.NewNotificationToken;

@Schema(description = "FCM 토큰 등록 요청 DTO")
@Builder
public record NotificationTokenRegisterRequest(
        @Schema(description = "플랫폼", example = "WEB")
        @NotNull NotificationPlatform platform,

        @Schema(description = "FCM 토큰", example = "fcm_token...")
        @NotBlank String token
) {

    public NewNotificationToken toNewDeviceToken() {
        return NewNotificationToken.of(platform, token);
    }
}