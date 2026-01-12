package org.squad.careerhub.domain.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.service.dto.UpdatePreference;

@Builder
public record NotificationPreferenceUpdateRequest(
        @Schema(description = "플랫폼", example = "WEB")
        @NotNull NotificationPlatform platform,

        @Schema(description = "이벤트", example = "INTERVIEW_1H_BEFORE")
        @NotNull NotificationEvent event,

        @Schema(description = "활성화 여부", example = "true")
        @NotNull Boolean enabled
) {

    public UpdatePreference toUpdatePreference() {
        return UpdatePreference.of(platform, event, enabled);
    }
}
