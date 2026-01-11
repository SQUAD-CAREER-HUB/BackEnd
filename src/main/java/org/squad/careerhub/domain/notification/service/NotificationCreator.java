package org.squad.careerhub.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.notification.entity.Notification;
import org.squad.careerhub.domain.notification.entity.NotificationPreference;
import org.squad.careerhub.domain.notification.entity.NotificationToken;
import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.enums.NotificationType;
import org.squad.careerhub.domain.notification.enums.SourceType;
import org.squad.careerhub.domain.notification.repository.NotificationDeviceJpaRepository;
import org.squad.careerhub.domain.notification.repository.NotificationJpaRepository;
import org.squad.careerhub.domain.notification.repository.NotificationPreferenceJpaRepository;
import org.squad.careerhub.domain.notification.service.dto.NewNotification;
import org.squad.careerhub.domain.notification.service.dto.NotificationResponse;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class NotificationCreator {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationDeviceJpaRepository notificationDeviceJpaRepository;
    private final NotificationPreferenceJpaRepository notificationPreferenceJpaRepository;
    private final PushSender pushSender;

    public NotificationResponse createAndSend(Long memberId, NewNotification notification) {
        // preference check (없으면 기본 ON)
        boolean enabled = notificationPreferenceJpaRepository
                .findByMemberIdAndEventAndStatus(memberId, notification.event(),
                        EntityStatus.ACTIVE)
                .map(NotificationPreference::isEnabled)
                .orElse(true);

        if (!enabled) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }

        Notification saved = notificationJpaRepository.save(
                Notification.createPending(
                        memberId,
                        notification.platform(),
                        notification.event(),
                        notification.sourceId(),
                        notification.title(),
                        notification.body(),
                        notification.linkUrl()
                )
        );

        List<NotificationToken> devices = notificationDeviceJpaRepository
                .findAllByMemberIdAndEnabledTrueAndStatus(memberId, EntityStatus.ACTIVE);

        // 디바이스가 없으면 DB만 저장
        if (devices.isEmpty()) {
            log.info("[NotificationCreator] no device. stored only. memberId={}", memberId);
            return NotificationResponse.from(saved);
        }

        int successCount = 0;

        for (NotificationToken d : devices) {
            try {
                pushSender.send(d.getToken(), saved.getTitle(), saved.getBody(),
                        saved.getLinkUrl());
                successCount++;
            } catch (Exception e) {
                log.warn("[NotificationCreator] push send failed. tokenId={}, memberId={}",
                        d.getId(), memberId, e);
            }
        }

        if (successCount > 0) {
            saved.markSent(LocalDateTime.now());
        }

        return NotificationResponse.from(saved);
    }

    public NotificationResponse sendTest(Long memberId, NotificationPlatform platform) {
        return createAndSend(
                memberId,
                NewNotification.create(
                        platform,
                        NotificationType.SYSTEM,
                        NotificationEvent.DEADLINE_D1,
                        SourceType.ETC,
                        0L,
                        "테스트 알림",
                        "FCM 테스트 메시지입니다.",
                        "/"
                )
        );
    }
}
