package org.squad.careerhub.domain.notification.service.dto;

import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;

public record UpdatePreference(
        NotificationPlatform platform,
        NotificationEvent event,
        boolean enabled
) {

    public static UpdatePreference of(NotificationPlatform platform, NotificationEvent event,
            boolean enabled) {
        return new UpdatePreference(platform, event, enabled);
    }
}
