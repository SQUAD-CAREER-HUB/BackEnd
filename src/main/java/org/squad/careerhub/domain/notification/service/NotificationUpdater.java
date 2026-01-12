package org.squad.careerhub.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.notification.entity.Notification;
import org.squad.careerhub.domain.notification.entity.NotificationPreference;
import org.squad.careerhub.domain.notification.entity.NotificationToken;
import org.squad.careerhub.domain.notification.repository.NotificationDeviceJpaRepository;
import org.squad.careerhub.domain.notification.repository.NotificationJpaRepository;
import org.squad.careerhub.domain.notification.repository.NotificationPreferenceJpaRepository;
import org.squad.careerhub.domain.notification.service.dto.NewNotificationToken;
import org.squad.careerhub.domain.notification.service.dto.UpdatePreference;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
@Transactional
public class NotificationUpdater {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationDeviceJpaRepository notificationDeviceJpaRepository;
    private final NotificationPreferenceJpaRepository notificationPreferenceJpaRepository;

    public void registerOrUpdateDevice(Long memberId, NewNotificationToken deviceToken) {
        NotificationToken notificationToken = notificationDeviceJpaRepository
                .findByMemberIdAndPlatformAndStatus(memberId, deviceToken.platform(),
                        EntityStatus.ACTIVE)
                .orElseGet(
                        () -> NotificationToken.register(
                                memberId,
                                deviceToken.platform(),
                                deviceToken.token()
                        )
                );

        notificationToken.updateToken(deviceToken.token());
        notificationToken.active();
        notificationDeviceJpaRepository.save(notificationToken);
    }

    public void markAsRead(Long memberId, Long notificationId) {
        Notification n = notificationJpaRepository
                .findByIdAndMemberIdAndStatus(notificationId, memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND));

        n.markRead();
    }

    public void markAllAsRead(Long memberId) {
        notificationJpaRepository.markAllAsRead(memberId, EntityStatus.ACTIVE);
    }

    public void updatePreference(Long memberId, UpdatePreference preference) {
        NotificationPreference pref = notificationPreferenceJpaRepository
                .findByMemberIdAndPlatformAndEventAndStatus(
                        memberId,
                        preference.platform(),
                        preference.event(),
                        EntityStatus.ACTIVE
                )
                .orElseGet(
                        () -> NotificationPreference.create(
                                memberId,
                                preference.platform(),
                                preference.event(),
                                preference.enabled()
                        )
                );

        pref.updateEnabled(preference.enabled());
        pref.active();
        notificationPreferenceJpaRepository.save(pref);
    }

    public void deleteNotification(Long notificationId, Long memberId) {
        Notification n = notificationJpaRepository
                .findByIdAndMemberId(notificationId, memberId)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND));

        if (n.isDeleted()) {
            return;
        }
        n.delete();
    }

    public void deleteNotificationToken(Long tokenId, Long memberId) {
        int updated = notificationDeviceJpaRepository.softDeleteByIdAndMemberId(
                memberId,
                tokenId,
                EntityStatus.ACTIVE,
                EntityStatus.DELETED);

        if (updated > 0) {
            return; // 204
        }

        boolean exists = notificationDeviceJpaRepository.existsById(tokenId);
        if (!exists) {
            throw new CareerHubException(ErrorStatus.NOT_FOUND);
        }
        throw new CareerHubException(ErrorStatus.FORBIDDEN_DELETE);
    }
}