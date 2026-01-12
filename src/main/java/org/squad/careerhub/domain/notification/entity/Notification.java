package org.squad.careerhub.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.enums.NotificationType;
import org.squad.careerhub.domain.notification.enums.SourceType;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_member_created", columnList = "member_id, created_at")
        }
)
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPlatform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SourceType sourceType;

    @Column(nullable = false)
    private Long sourceId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String body;

    // 예: "/applications/10", "/applications/10/schedules", "/community/reviews/3"
    @Column(length = 500)
    private String linkUrl;

    @Column(nullable = false)
    private Boolean is_read;

    private LocalDateTime sentAt;

    private Notification(
            Long memberId,
            NotificationPlatform platform,
            NotificationEvent event,
            NotificationType type,
            SourceType sourceType,
            Long sourceId,
            String title,
            String body,
            String linkUrl,
            Boolean is_read,
            LocalDateTime sentAt
    ) {
        this.memberId = memberId;
        this.platform = platform;
        this.event = event;
        this.type = type;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.title = title;
        this.body = body;
        this.linkUrl = linkUrl;
        this.is_read = is_read;
        this.sentAt = sentAt;
    }

    public static Notification createPending(
            Long memberId,
            NotificationPlatform platform,
            NotificationEvent event,
            Long sourceId,
            String title,
            String body,
            String linkUrl
    ) {
        return new Notification(
                memberId,
                platform,
                event,
                event.getNotificationType(),
                event.getSourceType(),
                sourceId,
                title,
                body,
                linkUrl,
                false,
                null
        );
    }

    public static Notification createSent(
            Long memberId,
            NotificationPlatform platform,
            NotificationEvent event,
            Long sourceId,
            String title,
            String body,
            String linkUrl,
            LocalDateTime sentAt
    ) {
        return new Notification(
                memberId,
                platform,
                event,
                event.getNotificationType(),
                event.getSourceType(),
                sourceId,
                title,
                body,
                linkUrl,
                false,
                sentAt
        );
    }

    public void markSent(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void markRead() {
        this.is_read = true;
    }

    public void changeLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
