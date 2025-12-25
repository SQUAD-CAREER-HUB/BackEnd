package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@Builder
public record NewInterviewSchedule(
    StageType stageType,
    String scheduleName,
    LocalDateTime startedAt,
    String location
) {
}