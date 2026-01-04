package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import org.squad.careerhub.domain.application.entity.StageType;

public record ScheduleItem(
        Long scheduleId,
        Long applicationId,
        String companyName,
        StageType stageType,
        String scheduleName,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String location
) {

}