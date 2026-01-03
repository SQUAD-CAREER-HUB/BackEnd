package org.squad.careerhub.domain.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.service.dto.UpdateEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.UpdateInterviewSchedule;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
@Transactional
public class ScheduleUpdater {

    private final ScheduleReader scheduleReader;
    private final ScheduleJpaRepository scheduleJpaRepository;

    public Schedule updateInterviewSchedule(Application app, Long scheduleId,
            UpdateInterviewSchedule dto) {
        Schedule schedule = scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId,
                        app.getId(),
                        StageType.INTERVIEW,
                        EntityStatus.ACTIVE
                )
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND));

        schedule.updateInterview(dto.scheduleName(), dto.startedAt(), dto.location(),
                dto.scheduleResult());
        return schedule;
    }

    public void deleteSchedule(Application app, Long scheduleId) {
        Schedule schedule = scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId,
                        app.getId(),
                        StageType.ETC,
                        EntityStatus.ACTIVE
                )
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND));

        schedule.delete();
    }

    public Schedule updateEtcSchedule(Application app, Long scheduleId, UpdateEtcSchedule dto) {
        Schedule schedule = scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId,
                        app.getId(),
                        StageType.ETC,
                        EntityStatus.ACTIVE
                )
                .orElseThrow(() -> new CareerHubException(ErrorStatus.NOT_FOUND));

        schedule.updateEtc(dto.scheduleName(), dto.startedAt(), dto.endedAt(),
                dto.scheduleResult());
        return schedule;
    }

}


