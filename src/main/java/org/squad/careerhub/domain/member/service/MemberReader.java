package org.squad.careerhub.domain.member.service;

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
        return memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND_MEMBER));
    }

    public Optional<Member> findMemberBySocial(SocialProvider provider, String socialId) {
        return memberJpaRepository.findBySocialProviderAndSocialIdAndStatus(provider, socialId, EntityStatus.ACTIVE);
    }

}