package org.squad.careerhub.domain.schedule.service;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.service.dto.NewDocumentSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
@Transactional
public class ScheduleManager {

    private final ScheduleJpaRepository scheduleJpaRepository;
    private final ApplicationStageJpaRepository applicationStageJpaRepository;

    public Schedule createInterviewSchedule(Application app, NewInterviewSchedule schedule) {
        return createSingle(app, StageType.INTERVIEW, stage ->
                Schedule.register(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        schedule.location(),
                        ScheduleResult.WAITING,
                        null,
                        requireNonNull(schedule.startedAt()),
                        null
                )
        );
    }

    public List<Schedule> createInterviewSchedules(Application app,
            List<NewInterviewSchedule> schedules) {
        return createBulk(app, StageType.INTERVIEW, schedules, schedule ->
                stage -> Schedule.register(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        schedule.location(),
                        ScheduleResult.WAITING,
                        null,
                        schedule.startedAt(),
                        null
                )
        );
    }

    public Schedule createEtcSchedule(Application app, NewEtcSchedule schedule) {
        return createSingle(app, StageType.ETC, stage ->
                Schedule.register(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        null,
                        ScheduleResult.WAITING,
                        null,
                        schedule.startedAt(),
                        schedule.endedAt()
                )
        );
    }

    public List<Schedule> createEtcSchedules(Application app, List<NewEtcSchedule> schedules) {
        return createBulk(app, StageType.ETC, schedules, schedule ->
                stage -> Schedule.register(
                        app.getAuthor(),
                        stage,
                        schedule.scheduleName(),
                        null,
                        ScheduleResult.WAITING,
                        null,
                        schedule.startedAt(),
                        schedule.endedAt()
                )
        );
    }

    public Schedule createDocumentSchedule(Application app, NewDocumentSchedule dto) {
        return createSingle(app, StageType.DOCUMENT, stage ->
                Schedule.register(
                        app.getAuthor(),
                        stage,
                        StageType.DOCUMENT.getDescription(),
                        null,
                        dto.scheduleResult(),
                        dto.submissionStatus(),
                        app.getDeadline(),
                        dto.endedAt()
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
            List<T> dtos,
            Function<T, Function<ApplicationStage, Schedule>> scheduleFactory
    ) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }

        ApplicationStage stage = prepareStage(app, stageType);

        List<Schedule> schedules = dtos.stream()
                .map(dto -> scheduleFactory.apply(dto).apply(stage))
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
                        }
                );
    }
}