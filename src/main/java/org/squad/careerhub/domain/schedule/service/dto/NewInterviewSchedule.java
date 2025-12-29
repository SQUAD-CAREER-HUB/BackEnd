package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NewInterviewSchedule(
    String scheduleName,
    LocalDateTime startedAt,
    String location
) {

}