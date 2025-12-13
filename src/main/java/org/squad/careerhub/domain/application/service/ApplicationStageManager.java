package org.squad.careerhub.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewEtcSchedule;
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
        String stageName = getStageName(newStage.newEtcSchedule(), stageType);

        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                stageType,
                stageName,
                newStage.submissionStatus()
        );

        return applicationStageJpaRepository.save(applicationStage);
    }

    private String getStageName(NewEtcSchedule newEtcSchedule, StageType stageType) {
        if (stageType != StageType.ETC) {
            return stageType.getDescription();
        }

        // 기타 전형인데 기타 일정이 null인 경우 기타 전형으로 저장
        return newEtcSchedule != null ? newEtcSchedule.stageName() : StageType.ETC.getDescription();
    }

}