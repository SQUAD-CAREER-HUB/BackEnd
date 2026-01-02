package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.UpdateApplication;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.service.MemberReader;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Slf4j
@Component
public class ApplicationManager {

    private final MemberReader memberReader;
    private final ApplicationJpaRepository applicationJpaRepository;

    @Transactional
    public Application create(
            NewApplication newApplication,
            Long authorId
    ) {
        Member author = memberReader.find(authorId);

        Application application = applicationJpaRepository.save(Application.create(
                author,
                newApplication.jobPostingUrl(),
                newApplication.company(),
                newApplication.position(),
                newApplication.jobLocation(),
                newApplication.stageType(),
                newApplication.finalApplicationStatus(),
                newApplication.applicationMethod(),
                newApplication.deadline()
        ));
        log.debug("[ApplicationManager] 지원서 생성 완료 - applicationId: {}", application.getId());

        return application;
    }

    @Transactional
    public Application updateApplication(UpdateApplication updateApplication, Long memberId) {
        Application application = applicationJpaRepository.findByIdAndAuthorId(updateApplication.applicationId(), memberId)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.FORBIDDEN_MODIFY));

        application.update(
                updateApplication.jobPostingUrl(),
                updateApplication.company(),
                updateApplication.position(),
                updateApplication.jobLocation(),
                updateApplication.memo()
        );

        log.debug("[ApplicationManager] 지원서 수정 완료 - applicationId: {}", application.getId());

        return application;
    }

}