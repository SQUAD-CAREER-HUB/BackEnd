package org.squad.careerhub.domain.schedule.service;

import static java.util.Objects.requireNonNull;

import java.util.List;
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

    public Schedule createInterviewSchedule(Application app,
        NewInterviewSchedule interviewSchedule) {
        ApplicationStage stage = getOrCreateStage(app, StageType.INTERVIEW);
        app.updateCurrentStageType(StageType.INTERVIEW);

        Schedule schedule = Schedule.register(
            app.getAuthor(),
            stage,
            interviewSchedule.scheduleName(),
            interviewSchedule.location(),
            ScheduleResult.WAITING,
            null,
            requireNonNull(interviewSchedule.startedAt()),
            null
        );
        return scheduleJpaRepository.save(schedule);
    }

    public void createInterviewSchedules(Application app,
        List<NewInterviewSchedule> interviewSchedules) {
        ApplicationStage stage = getOrCreateStage(app, StageType.INTERVIEW);
        app.updateCurrentStageType(StageType.INTERVIEW);

        List<Schedule> schedules = interviewSchedules.stream()
            .map(schedule -> Schedule.register(
                app.getAuthor(),
                stage,
                schedule.scheduleName(),
                schedule.location(),
                ScheduleResult.WAITING,
                null,
                requireNonNull(schedule.startedAt()),
                null
            ))
            .toList();

        scheduleJpaRepository.saveAll(schedules);
    }

    public Schedule createEtcSchedule(Application app, NewEtcSchedule etcSchedule) {
        ApplicationStage stage = getOrCreateStage(app, StageType.ETC);
        app.updateCurrentStageType(StageType.ETC);

        Schedule schedule = Schedule.register(
            app.getAuthor(),
            stage,
            requireNonNull(etcSchedule.scheduleName()),
            null,
            ScheduleResult.WAITING,
            null,
            requireNonNull(etcSchedule.startedAt()),
            etcSchedule.endedAt()
        );

        return scheduleJpaRepository.save(schedule);
    }

    private ApplicationStage getOrCreateStage(Application app, StageType stageType) {
        if (stageType == null) {
            throw new CareerHubException(ErrorStatus.BAD_REQUEST);
        }

        return applicationStageJpaRepository
            .findByApplicationIdAndStageType(app.getId(), stageType)
            .orElseGet(() -> {
                try {
                    ApplicationStage created = ApplicationStage.create(
                        app,
                        stageType
                    );
                    return applicationStageJpaRepository.save(created);
                } catch (DataIntegrityViolationException e) {
                    return applicationStageJpaRepository
                        .findByApplicationIdAndStageType(app.getId(), stageType)
                        .orElseThrow(() -> e);
                }
            });
    }

    public Schedule createDocumentSchedule(Application app, NewDocumentSchedule documentSchedule) {
        ApplicationStage stage = getOrCreateStage(app, StageType.DOCUMENT);
        app.updateCurrentStageType(StageType.DOCUMENT);

        Schedule schedule = Schedule.register(
            app.getAuthor(),
            stage,
            StageType.DOCUMENT.getDescription(),
            null,
            ScheduleResult.WAITING,
            documentSchedule.submissionStatus(),
            app.getDeadline(),
            documentSchedule.endedAt()
        );

        return scheduleJpaRepository.save(schedule);
    }
}