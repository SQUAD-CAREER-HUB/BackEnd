package org.squad.careerhub.domain.member.service;

import jakarta.persistence.PessimisticLockException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class MemberReader {

    private final MemberJpaRepository memberJpaRepository;

    public Member find(Long memberId) {
        return memberJpaRepository.findByIdAndStatus(memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_MEMBER));
    }

    public Optional<Member> findMemberBySocial(SocialProvider provider, String socialId) {
        return memberJpaRepository.findBySocialProviderAndSocialIdAndStatus(provider, socialId, EntityStatus.ACTIVE);
    }


    /**
     * Refresh Token 으로 회원을 조회하면서 비관적 락을 획득합니다.
     *
     * <p>동시성 제어:
     * <ul>
     *   <li>동일한 토큰으로 동시 요청 시 하나만 성공</li>
     *   <li>락 획득 실패 → CONCURRENT_REQUESTS_LIMIT_EXCEEDED</li>
     *   <li>토큰을 가지고 있는 회원 없음 → NOT_FOUND_ACTIVE_MEMBER_BY_REFRESH_TOKEN (이미 업데이트 되거나 존재하지 않음)</li>
     * </ul>
     *
     * <p>참고: 트랜잭션이 빠르게 완료되기에 대부분 NOT_FOUND_ACTIVE_MEMBER_BY_REFRESH_TOKEN 발생
     */
    public Member findByRefreshTokenWithLock(String refreshToken) {
        // NOTE: refreshToken index 필요성 검토
        try {
            return memberJpaRepository.findByRefreshTokenAndStatusWithLock(refreshToken, EntityStatus.ACTIVE)
                    .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_ACTIVE_MEMBER_BY_REFRESH_TOKEN));
        } catch (PessimisticLockException e) {
            throw new CareerHubException(ErrorStatus.CONCURRENT_REQUESTS_LIMIT_EXCEEDED);
        }
    }

}