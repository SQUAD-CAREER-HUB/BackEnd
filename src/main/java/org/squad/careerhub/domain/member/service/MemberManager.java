package org.squad.careerhub.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class MemberManager {

    private final MemberJpaRepository memberJpaRepository;

    public Member create(Member member) {
        return memberJpaRepository.save(member);
    }

    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken) {
        Member member = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_MEMBER));

        member.updateRefreshToken(refreshToken);
    }

}