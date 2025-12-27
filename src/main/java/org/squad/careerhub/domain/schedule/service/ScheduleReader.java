package org.squad.careerhub.domain.schedule.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;

@RequiredArgsConstructor
@Component
public class ScheduleReader {

    private final ScheduleJpaRepository scheduleJpaRepository;

    public List<Schedule> findSchedule(List<ApplicationStage> applicationStages, Long authorId) {
        return scheduleJpaRepository.findByApplicationStageInAndAuthorId(applicationStages, authorId);
    }

}