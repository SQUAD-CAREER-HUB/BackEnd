package org.squad.careerhub.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.service.dto.NewNotification;
import org.squad.careerhub.domain.notification.service.dto.NewNotificationToken;
import org.squad.careerhub.domain.notification.service.dto.NotificationPageResponse;
import org.squad.careerhub.domain.notification.service.dto.NotificationPreferenceListResponse;
import org.squad.careerhub.domain.notification.service.dto.NotificationResponse;
import org.squad.careerhub.domain.notification.service.dto.UpdatePreference;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationCreator notificationCreator;
    private final NotificationUpdater notificationUpdater;
    private final NotificationReader notificationReader;

    /**
     * 디바이스 토큰 등록/갱신
     */
    @Transactional
    public void registerDevice(Long memberId, NewNotificationToken deviceToken) {
        notificationUpdater.registerOrUpdateDevice(memberId, deviceToken);
        log.info("[NotificationService] registerDevice done. memberId={}, platform={}", memberId,
                deviceToken.platform());
    }

    /**
     * 알림 생성 + 전송(FCM)
     */
    @Transactional
    public NotificationResponse createAndSend(Long memberId, NewNotification notification) {
        NotificationResponse res = notificationCreator.createAndSend(memberId, notification);
        log.info("[NotificationService] createAndSend done. memberId={}, event={}", memberId,
                notification.event());
        return res;
    }

    /**
     * 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public NotificationPageResponse getMyNotifications(Long cursorId, int size, Long memberId) {
        int safeSize = Math.min(Math.max(size, 1), 20);
        return notificationReader.getMyNotifications(memberId, cursorId, safeSize);
    }

    /**
     * 단건 읽음 처리 (멱등)
     */
    @Transactional
    public void markAsRead(Long memberId, Long notificationId) {
        notificationUpdater.markAsRead(memberId, notificationId);
        log.info("[NotificationService] markAsRead done. memberId={}, notificationId={}", memberId,
                notificationId);
    }

    /**
     * 전체 읽음 처리
     */
    @Transactional
    public void markAllAsRead(Long memberId) {
        notificationUpdater.markAllAsRead(memberId);
        log.info("[NotificationService] markAllAsRead done. memberId={}", memberId);
    }

    /**
     * 알림 설정 토글(이벤트 단위)
     */
    @Transactional
    public void updatePreference(Long memberId, UpdatePreference preference) {
        notificationUpdater.updatePreference(memberId, preference);
        log.info("[NotificationService] updatePreference done. memberId={}, event={}, enabled={}",
                memberId, preference.event(), preference.enabled());
    }

    /**
     * 알림 논리삭제 (멱등)
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long memberId) {
        notificationUpdater.deleteNotification(notificationId, memberId);
        log.info("[NotificationService] deleteNotification done. memberId={}, notificationId={}",
                memberId, notificationId);
    }

    @Transactional
    public void deleteNotificationToken(Long tokenId, Long memberId) {
        notificationUpdater.deleteNotificationToken(tokenId, memberId);
        log.info("[NotificationService] deleteNotificationToken done. memberId={}, tokenId={}",
                memberId, tokenId);
    }

    @Transactional(readOnly = true)
    public NotificationPreferenceListResponse getPreferences(
            Long memberId,
            NotificationPlatform platform) {
        return notificationReader.getPreferences(memberId, platform);
    }

    @Transactional
    public void sendTest(Long memberId, NotificationPlatform platform) {
        notificationCreator.sendTest(memberId, platform);
    }
}