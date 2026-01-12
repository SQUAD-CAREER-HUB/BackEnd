package org.squad.careerhub.domain.notification.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.squad.careerhub.domain.notification.entity.Notification;
import org.squad.careerhub.global.entity.EntityStatus;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByIdAndMemberId(Long id, Long memberId);

    Optional<Notification> findByIdAndMemberIdAndStatus(Long id, Long memberId,
            EntityStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update Notification n
                   set n.is_read = true
                 where n.memberId = :memberId
                   and n.status = :status
                   and n.is_read = false
            """)
    void markAllAsRead(@Param("memberId") Long memberId, @Param("status") EntityStatus status);
}