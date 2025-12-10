package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.service.MemberReader;

@RequiredArgsConstructor
@Component
public class ApplicationManager {

    private final ApplicationJpaRepository applicationJpaRepository;
    private final MemberReader memberReader;

    public Application create(
            NewJobPosting newJobPosting,
            NewApplicationInfo newApplicationInfo,
            Long memberId
    ) {
        Member author = memberReader.find(memberId);

        return applicationJpaRepository.save(Application.create(
                author,
                newJobPosting.jobPostingUrl(),
                newJobPosting.company(),
                newJobPosting.position(),
                newJobPosting.jobLocation(),
                newApplicationInfo.applicationStatus(),
                newApplicationInfo.applicationMethod(),
                newApplicationInfo.deadline(),
                newApplicationInfo.submittedAt(),
                newApplicationInfo.memo()
        ));
    }

}