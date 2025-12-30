package org.squad.careerhub.domain.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.schedule.service.ScheduleManager;
import org.squad.careerhub.domain.schedule.service.dto.NewDocumentSchedule;

@RequiredArgsConstructor
@Component
public class ApplicationStageManager {

    private final ScheduleManager scheduleManager;
    private final ApplicationStageJpaRepository applicationStageJpaRepository;

    // NOTE: null 반환 하는 게 맘에 안듦 추후에 고민해봄
    @Transactional
    public ApplicationStage createWithSchedule(Application application, NewStage newStage) {
        StageType stageType = newStage.stageType();

        if (stageType.isApplicationClose()) {
            return null;
        }

        // 면접, 기타 전형 생성 시 서류 전형이 없으면 함께 생성
        if (!stageType.isDocument() && !hasDocumentStage(application)) {
            applicationStageJpaRepository.save(ApplicationStage.create(
                    application,
                    StageType.DOCUMENT
            ));
        }

        // 전형 생성 후 일정 생성
        // NOTE: 추후 리팩토링이 필요함. switch 문이 좋지 않음
        return switch (stageType) {
            case DOCUMENT -> createDocumentStage(application, newStage);
            case ETC -> createEtcStage(application, newStage);
            case INTERVIEW -> createInterviewStage(application, newStage);
            case APPLICATION_CLOSE -> null;
        };
    }

    private boolean hasDocumentStage(Application application) {
        return applicationStageJpaRepository.existsByApplicationAndStageType(application,
                StageType.DOCUMENT);
    }

    private ApplicationStage createInterviewStage(Application application, NewStage newStage) {
        ApplicationStage interviewStage = applicationStageJpaRepository.save(
                ApplicationStage.create(
                        application,
                        StageType.INTERVIEW
                ));
        scheduleManager.createInterviewSchedules(application, newStage.newInterviewSchedules());

        return interviewStage;
    }

    private ApplicationStage createDocumentStage(Application application, NewStage newStage) {
        ApplicationStage documentStage = applicationStageJpaRepository.save(ApplicationStage.create(
                application,
                StageType.DOCUMENT
        ));
        scheduleManager.createDocumentSchedule(application,
                new NewDocumentSchedule(
                        application.getDeadline(),
                        newStage.submissionStatus(),
                        ScheduleResult.WAITING // 추가될거로 예상해서 미리 넣어둠 stage에서 값 가져오는 걸로 변경해야함
                )
        );

        return documentStage;
    }

    private ApplicationStage createEtcStage(Application application, NewStage newStage) {
        ApplicationStage etcStage = applicationStageJpaRepository.save(ApplicationStage.create(
                application,
                newStage.stageType()
        ));
        scheduleManager.createEtcSchedule(application, newStage.newEtcSchedules().getFirst());

        return etcStage;
    }

}