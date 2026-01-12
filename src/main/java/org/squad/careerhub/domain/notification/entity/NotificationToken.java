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
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "notification_token",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notification_token_member_platform_token",
                columnNames = {"member_id", "platform", "token"}
        )
)
public class NotificationToken extends BaseEntity {

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPlatform platform;

    @Column(nullable = false, length = 512)
    private String token;

    @Column(nullable = false)
    private boolean enabled;

    private NotificationToken(Long memberId, NotificationPlatform platform, String token,
            boolean enabled) {
        this.memberId = memberId;
        this.platform = platform;
        this.token = token;
        this.enabled = enabled;
    }

    public static NotificationToken register(Long memberId, NotificationPlatform platform,
            String token) {
        return new NotificationToken(memberId, platform, token, true);
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
