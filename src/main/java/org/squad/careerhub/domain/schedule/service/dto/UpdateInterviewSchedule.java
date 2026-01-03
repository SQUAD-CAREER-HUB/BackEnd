package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ScheduleResult;

@Builder
public record UpdateInterviewSchedule(
        String scheduleName,
        LocalDateTime startedAt,
        String location,
        ScheduleResult scheduleResult
) {

}
