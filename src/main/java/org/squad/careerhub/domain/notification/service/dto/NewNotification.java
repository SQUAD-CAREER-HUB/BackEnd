package org.squad.careerhub.domain.notification.service.dto;

import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.enums.NotificationType;
import org.squad.careerhub.domain.notification.enums.SourceType;

public record NewNotification(
        NotificationPlatform platform,
        NotificationType type,
        NotificationEvent event,
        SourceType sourceType,
        Long sourceId,
        String title,
        String body,
        String linkUrl
) {

    public static NewNotification create(
            NotificationPlatform platform,
            NotificationType type,
            NotificationEvent event,
            SourceType sourceType,
            Long sourceId,
            String title,
            String body,
            String linkUrl
    ) {
        return new NewNotification(platform, type, event, sourceType, sourceId, title,
                body, linkUrl);
    }
}
