package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NewEtcSchedule(
    String scheduleName,
    LocalDateTime startedAt,
    LocalDateTime endedAt
) {

}
