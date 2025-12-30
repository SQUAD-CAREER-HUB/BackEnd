package org.squad.careerhub.domain.schedule.service;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.service.dto.NewDocsSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

// 리팩토링 대상!! 중복 코드가 너무 많음
@RequiredArgsConstructor
@Component
@Transactional
public class ScheduleCreator {

    private final ScheduleJpaRepository scheduleJpaRepository;
    private final ApplicationStageJpaRepository applicationStageJpaRepository;

    public Schedule createInterviewSchedule(Application app, NewInterviewSchedule schedule) {
        return createSingle(app, StageType.INTERVIEW, stage ->
                Schedule.registerInterview(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        schedule.location(),
                        schedule.scheduleResult(),
                        schedule.startedAt()
                )
        );
    }

    public List<Schedule> createInterviewSchedules(Application app, List<NewInterviewSchedule> schedules) {
        return createBulk(app, StageType.INTERVIEW, schedules, schedule ->
                stage -> Schedule.registerInterview(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        schedule.location(),
                        schedule.scheduleResult(),
                        schedule.startedAt()
                )
        );
    }

    public Schedule createEtcSchedule(Application app, NewEtcSchedule schedule) {
        return createSingle(app, StageType.ETC, stage ->
                Schedule.registerEtc(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        schedule.scheduleResult(),
                        schedule.startedAt(),
                        schedule.endedAt()
                )
        );
    }

    public List<Schedule> createEtcSchedules(Application app, List<NewEtcSchedule> schedules) {
        return createBulk(app, StageType.ETC, schedules, schedule ->
                stage -> Schedule.registerEtc(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        schedule.scheduleResult(),
                        schedule.startedAt(),
                        schedule.endedAt()
                )
        );
    }

    public Schedule createDocumentSchedule(Application app, NewDocsSchedule schedule) {
        return createSingle(app, StageType.DOCUMENT, stage ->
                Schedule.registerDocs(
                        app.getAuthor(),
                        stage,
                        StageType.DOCUMENT.getDescription(),
                        schedule.submissionStatus(),
                        schedule.scheduleResult(),
                        app.getDeadline(), // startedAt 가정은 deadline과 같습니다
                        app.getDeadline() // endedAt 가정은 deadline과 같습니다
                )
        );
    }

    private Schedule createSingle(
            Application app,
            StageType stageType,
            Function<ApplicationStage, Schedule> scheduleFactory
    ) {
        ApplicationStage stage = prepareStage(app, stageType);
        Schedule schedule = scheduleFactory.apply(stage);

        return scheduleJpaRepository.save(schedule);
    }

    private <T> List<Schedule> createBulk(
            Application app,
            StageType stageType,
            List<T> targets,
            Function<T, Function<ApplicationStage, Schedule>> scheduleFactory
    ) {
        if (targets == null || targets.isEmpty()) {
            return List.of();
        }

        ApplicationStage stage = prepareStage(app, stageType);

        List<Schedule> schedules = targets.stream()
                .map(target -> scheduleFactory.apply(target).apply(stage))
                .toList();

        return scheduleJpaRepository.saveAll(schedules);
    }

    private ApplicationStage prepareStage(Application app, StageType stageType) {
        ApplicationStage stage = getOrCreateStage(app, stageType);
        app.updateCurrentStageType(stageType);
        return stage;
    }

    private ApplicationStage getOrCreateStage(Application app, StageType stageType) {
        if (stageType == null) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }

        return applicationStageJpaRepository
                .findByApplicationIdAndStageType(app.getId(), stageType)
                .orElseGet(() -> {
                    try {
                        ApplicationStage created = ApplicationStage.create(app, stageType);

                        return applicationStageJpaRepository.save(created);
                    } catch (DataIntegrityViolationException e) {
                        return applicationStageJpaRepository
                                .findByApplicationIdAndStageType(app.getId(), stageType)
                                .orElseThrow(() -> e);
                    }
                });
    }

}