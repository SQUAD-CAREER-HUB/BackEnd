package org.squad.careerhub.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "notification_preference",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pref_member_platform_event",
                columnNames = {"member_id", "platform", "event"}
        )
)
public class NotificationPreference extends BaseEntity {

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPlatform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationEvent event;

    @Column(nullable = false)
    private boolean enabled;

    private NotificationPreference(
            Long memberId,
            NotificationPlatform platform,
            NotificationEvent event,
            boolean enabled
    ) {
        this.memberId = memberId;
        this.platform = platform;
        this.event = event;
        this.enabled = enabled;
    }

    public static NotificationPreference createDefaultEnabled(
            Long memberId,
            NotificationPlatform platform,
            NotificationEvent event
    ) {
        return new NotificationPreference(memberId, platform, event, true);
    }

    public static NotificationPreference create(
            Long memberId,
            NotificationPlatform platform,
            NotificationEvent event,
            boolean enabled
    ) {
        return new NotificationPreference(memberId, platform, event, enabled);
    }

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
