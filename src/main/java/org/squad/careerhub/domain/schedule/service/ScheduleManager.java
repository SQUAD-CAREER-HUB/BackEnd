package org.squad.careerhub.domain.schedule.service;

import static java.util.Objects.requireNonNull;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
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
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    public Schedule createInterviewSchedule(Application app, NewInterviewSchedule cmd) {
        ApplicationStage stage = getOrCreateStage(app, StageType.INTERVIEW);

        app.updateCurrentStageType(StageType.INTERVIEW);

        Schedule schedule = Schedule.register(
            app.getAuthor(),
            stage,
            requireNonNull(cmd.scheduleName()),
            cmd.location(),
            ScheduleResult.WAITING,
            null,
            requireNonNull(cmd.startedAt()),
            null
        );
        return scheduleJpaRepository.save(schedule);
    }

    public void createInterviewSchedules(Application app, List<NewInterviewSchedule> cmds){
        ApplicationStage stage = getOrCreateStage(app, StageType.INTERVIEW);

        app.updateCurrentStageType(StageType.INTERVIEW);

        List<Schedule> schedules = cmds.stream()
            .map(cmd -> Schedule.register(
                app.getAuthor(),
                stage,
                requireNonNull(cmd.scheduleName()),
                cmd.location(),
                ScheduleResult.WAITING,
                null,
                requireNonNull(cmd.startedAt()),
                null
            ))
            .toList();

        scheduleJpaRepository.saveAll(schedules);
    }

    public Schedule createEtcSchedule(Application app, NewEtcSchedule cmd) {
        ApplicationStage stage = getOrCreateStage(app, StageType.ETC);
        app.updateCurrentStageType(StageType.ETC);

        Schedule schedule = Schedule.register(
            app.getAuthor(),
            stage,
            requireNonNull(cmd.scheduleName()),
            null,
            ScheduleResult.WAITING,
            null,
            requireNonNull(cmd.startedAt()),
            cmd.endedAt()
        );

        return scheduleJpaRepository.save(schedule);
    }

    private ApplicationStage getOrCreateStage(Application app, StageType stageType) {
        if (stageType == null) throw new CareerHubException(ErrorStatus.BAD_REQUEST);

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

    public Schedule createDocumentSchedule(Application app, NewDocumentSchedule cmd) {
        ApplicationStage stage = getOrCreateStage(app, StageType.DOCUMENT);

        app.updateCurrentStageType(StageType.DOCUMENT);

        Schedule schedule = Schedule.register(
            app.getAuthor(),
            stage,
            StageType.DOCUMENT.getDescription(),
            null,
            ScheduleResult.WAITING,
            cmd.submissionStatus(),
            app.getDeadline(),
            cmd.endedAt()
        );

        return scheduleJpaRepository.save(schedule);
    }
}