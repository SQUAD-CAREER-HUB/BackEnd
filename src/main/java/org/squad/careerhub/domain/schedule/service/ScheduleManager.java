package org.squad.careerhub.domain.schedule.service;

import static java.util.Objects.requireNonNull;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

@RequiredArgsConstructor
@Component
public class ScheduleManager {

    private final ScheduleJpaRepository scheduleJpaRepository;

    /**
     * 면접 전형일 경우 면접 일정 생성
     */
    public Schedule createInterviewSchedule(
        Application application,
        NewInterviewSchedule newInterviewSchedule
    ) {
        requireNonNull(newInterviewSchedule);

        return scheduleJpaRepository.save(Schedule.interviewCreate(
            application,
            StageType.INTERVIEW,
            newInterviewSchedule.interviewType(),
            newInterviewSchedule.typeDetail(),
            newInterviewSchedule.scheduledAt(),
            newInterviewSchedule.location(),
            newInterviewSchedule.link(),
            StageStatus.WAITING,
            application.getApplicationStatus()
        ));
    }

    public void createInterviewSchedules(
        Application application,
        List<NewInterviewSchedule> newInterviewSchedules
    ) {
        requireNonNull(application);

        List<Schedule> schedules = newInterviewSchedules.stream()
            .map(cmd -> createInterviewSchedule(application, cmd))
            .toList();

        scheduleJpaRepository.saveAll(schedules);
    }


    /**
     * 기타 전형일 경우 기타 유형 일정 생성
     */
    public Schedule createEtcSchedule(
        Application application,
        NewEtcSchedule newEtcSchedule
    ) {
        requireNonNull(application);

        return scheduleJpaRepository.save(Schedule.etcCreate(
            application,
            StageType.ETC,
            requireNonNull(newEtcSchedule.stageName()), // 기타일정 제목
            newEtcSchedule.scheduledAt(),
            newEtcSchedule.location(),
            newEtcSchedule.link(),
            StageStatus.WAITING,
            null,
            application.getApplicationStatus()
        ));
    }
}