package org.squad.careerhub.domain.notification.service.dto;

import org.squad.careerhub.domain.notification.enums.NotificationPlatform;

public record NewNotificationToken(
        NotificationPlatform platform,
        String token
) {

    public static NewNotificationToken of(NotificationPlatform platform, String token) {
        return new NewNotificationToken(platform, token);
    }
}