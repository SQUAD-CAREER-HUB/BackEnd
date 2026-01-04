package org.squad.careerhub.domain.member.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.global.entity.EntityStatus;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m " +
            "WHERE m.socialAccount.provider = :provider AND " +
            "m.socialAccount.socialId = :socialId AND " +
            "m.status = :status"
    )
    Optional<Member> findBySocialProviderAndSocialIdAndStatus(
            @Param("provider") SocialProvider provider,
            @Param("socialId") String socialId,
            @Param("status") EntityStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE) // Refresh Token 동시성 문제 해결을 위한 Lock 설정
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0")}) // 동시 요청 시 대기하지 않고 즉시 실패시키기 위한 설정
    @Query("SELECT m FROM Member m WHERE m.refreshToken = :refreshToken AND m.status = :status")
    Optional<Member> findByRefreshTokenAndStatusWithLock(
            @Param("refreshToken") String refreshToken,
            @Param("status") EntityStatus status
    );

    Optional<Member> findByIdAndStatus(Long memberId, EntityStatus entityStatus);

    Optional<Member> findByRefreshTokenAndStatus(String refreshToken, EntityStatus entityStatus);
}