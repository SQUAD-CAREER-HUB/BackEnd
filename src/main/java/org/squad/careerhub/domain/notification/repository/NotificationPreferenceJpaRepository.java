package org.squad.careerhub.domain.notification.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.notification.entity.NotificationPreference;
import org.squad.careerhub.domain.notification.enums.NotificationEvent;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.global.entity.EntityStatus;


public interface NotificationPreferenceJpaRepository extends
        JpaRepository<NotificationPreference, Long> {

    Optional<NotificationPreference> findByMemberIdAndEventAndStatus(
            Long memberId,
            NotificationEvent event,
            EntityStatus status
    );

    List<NotificationPreference> findAllByMemberIdAndPlatformAndStatus(
            Long memberId,
            NotificationPlatform platform,
            EntityStatus status
    );


    Optional<NotificationPreference> findByMemberIdAndPlatformAndEventAndStatus(
            Long memberId,
            NotificationPlatform platform,
            NotificationEvent event,
            EntityStatus entityStatus
    );
}
