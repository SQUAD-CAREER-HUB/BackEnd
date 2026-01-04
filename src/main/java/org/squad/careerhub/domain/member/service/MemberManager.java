package org.squad.careerhub.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Slf4j
@Component
public class MemberManager {

    private final MemberJpaRepository memberJpaRepository;

    public Member create(Member member) {
        Member savedMember = memberJpaRepository.save(member);

        log.debug("[MemberManager] 회원 생성 완료 - memberId: {}", savedMember.getId());

        return savedMember;
    }

    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken) {
        Member member = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_MEMBER));

        member.updateRefreshToken(refreshToken);

        log.debug("[MemberManager] RefreshToken 업데이트 완료 - memberId: {}", memberId);
    }

}

