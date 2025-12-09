package org.squad.careerhub.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    Optional<Member> findByIdAndStatus(Long memberId, EntityStatus entityStatus);

}