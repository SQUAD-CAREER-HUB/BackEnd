package org.squad.careerhub.domain.notification.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.notification.entity.Notification;
import org.squad.careerhub.domain.notification.entity.NotificationPreference;
import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.domain.notification.repository.NotificationPreferenceJpaRepository;
import org.squad.careerhub.domain.notification.repository.NotificationQueryDslRepository;
import org.squad.careerhub.domain.notification.service.dto.NotificationPageResponse;
import org.squad.careerhub.domain.notification.service.dto.NotificationPreferenceListResponse;
import org.squad.careerhub.domain.notification.service.dto.NotificationPreferenceResponse;
import org.squad.careerhub.domain.notification.service.dto.NotificationResponse;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class NotificationReader {

    private final NotificationPreferenceJpaRepository notificationPreferenceJpaRepository;
    private final NotificationQueryDslRepository notificationQueryDslRepository;

    public NotificationPageResponse getMyNotifications(Long memberId, Long cursorId, int size) {
        List<Notification> fetched = notificationQueryDslRepository
                .findMyNotifications(memberId, cursorId, size);

        boolean hasNext = fetched.size() > size;
        List<Notification> slice = hasNext ? fetched.subList(0, size) : fetched;

        List<NotificationResponse> items = slice.stream()
                .map(NotificationResponse::from)
                .toList();

        return NotificationPageResponse.from(items, hasNext);
    }

    public NotificationPreferenceListResponse getPreferences(Long memberId,
            NotificationPlatform platform) {

        // DB에 저장된 설정 (sparse)
        List<NotificationPreference> saved = notificationPreferenceJpaRepository
                .findAllByMemberIdAndPlatformAndStatus(memberId, platform, EntityStatus.ACTIVE);

        Map<NotificationEvent, Boolean> enabledMap = saved.stream()
                .collect(Collectors.toMap(
                                NotificationPreference::getEvent,
                                NotificationPreference::isEnabled
                        )
                );

        // enum 전체를 기준으로 응답 합성 (없으면 기본 true)
        List<NotificationPreferenceResponse> items = Arrays.stream(NotificationEvent.values())
                .map(event -> NotificationPreferenceResponse.of(
                        event,
                        platform,
                        enabledMap.getOrDefault(event, true)
                ))
                .toList();

        return NotificationPreferenceListResponse.from(items);
    }
}
