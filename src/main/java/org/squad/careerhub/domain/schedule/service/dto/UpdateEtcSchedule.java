package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ScheduleResult;

@Builder
public record UpdateEtcSchedule(
        String scheduleName,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        ScheduleResult scheduleResult
) {

}
