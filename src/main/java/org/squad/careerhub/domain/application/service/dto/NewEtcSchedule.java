package org.squad.careerhub.domain.application.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NewEtcSchedule(
        String stageName,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {

}