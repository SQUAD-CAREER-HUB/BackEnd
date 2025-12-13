package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewStage;

@RequiredArgsConstructor
@Component
public class ApplicationStageManager {

    private final ApplicationStageJpaRepository applicationStageJpaRepository;

    public ApplicationStage create(Application application, NewStage newStage) {
        StageType stageType = newStage.stageType();

        // 서류 전형이 아닌 다른 전형일 경우 서류 전형이 통과(PASS)로 자동 저장
        if (stageType != StageType.DOCUMENT) {
            ApplicationStage documentStage = ApplicationStage.createPassedDocumentStage(application);
            applicationStageJpaRepository.save(documentStage);
        }

        // 기타 전형은 사용자가 입력한 전형명으로 저장
        String stageName = stageType == StageType.ETC ?
                newStage.newEtcSchedule().stageName() : stageType.getDescription();

        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                stageType,
                stageName,
                newStage.submissionStatus()
        );

        return applicationStageJpaRepository.save(applicationStage);
    }

}