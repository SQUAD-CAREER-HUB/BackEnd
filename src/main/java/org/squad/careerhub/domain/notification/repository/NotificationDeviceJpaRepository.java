package org.squad.careerhub.domain.notification.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.squad.careerhub.domain.notification.entity.NotificationToken;
import org.squad.careerhub.domain.notification.enums.NotificationPlatform;
import org.squad.careerhub.global.entity.EntityStatus;

public interface NotificationDeviceJpaRepository extends JpaRepository<NotificationToken, Long> {

    Optional<NotificationToken> findByMemberIdAndPlatformAndStatus(
            Long memberId,
            NotificationPlatform platform,
            EntityStatus status
    );

    List<NotificationToken> findAllByMemberIdAndEnabledTrueAndStatus(Long memberId,
            EntityStatus status);

    Optional<NotificationToken> findByIdAndMemberIdAndStatus(Long id, Long memberId,
            EntityStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update NotificationToken dt
               set dt.status = :deleted
             where dt.id = :tokenId
               and dt.memberId = :memberId
               and dt.status = :active
            """)
    int softDeleteByIdAndMemberId(
            @Param("tokenId") Long tokenId,
            @Param("memberId") Long memberId,
            @Param("active") EntityStatus active,
            @Param("deleted") EntityStatus deleted
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update NotificationToken dt
               set dt.enabled = false
             where dt.memberId = :memberId
               and dt.status = :active
            """)
    int disableAllByMemberId(@Param("memberId") Long memberId,
            @Param("active") EntityStatus active);
}
