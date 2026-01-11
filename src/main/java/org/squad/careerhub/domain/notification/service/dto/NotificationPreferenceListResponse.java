package org.squad.careerhub.domain.notification.service.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record NotificationPreferenceListResponse(
        List<NotificationPreferenceResponse> preferences
) {

    public static NotificationPreferenceListResponse from(
            List<NotificationPreferenceResponse> items) {
        return NotificationPreferenceListResponse.builder()
                .preferences(items)
                .build();
    }
}
