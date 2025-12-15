package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.service.MemberReader;

@RequiredArgsConstructor
@Component
public class ApplicationManager {

    private final ApplicationJpaRepository applicationJpaRepository;
    private final MemberReader memberReader;
    private final ApplicationStageManager applicationStageManager;

    /**
     * 지원서와 해당 전형을 생성합니다.
     **/
    @Transactional
    public Application createWithStage(
            NewJobPosting newJobPosting,
            NewApplicationInfo newApplicationInfo,
            NewStage newStage,
            Long memberId
    ) {
        Member author = memberReader.find(memberId);

        Application application = applicationJpaRepository.save(Application.create(
                author,
                newJobPosting.jobPostingUrl(),
                newJobPosting.company(),
                newJobPosting.position(),
                newJobPosting.jobLocation(),
                newStage.stageType(),
                newApplicationInfo.applicationMethod(),
                newApplicationInfo.deadline(),
                newApplicationInfo.submittedAt()
        ));
        // NOTE: 최종 합격 또는 불합격 일 경우엔 전형 단계 생성이 불필요하다고 생각하여 생성하지 않음
        createStageIfNotFinal(application, newStage);

        return application;
    }

    private void createStageIfNotFinal(Application application, NewStage stage) {
        if (!stage.stageType().isFinalStage()) {
            applicationStageManager.create(application, stage);
        }
    }

}