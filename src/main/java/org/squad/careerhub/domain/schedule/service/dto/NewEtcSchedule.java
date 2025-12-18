package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageType;

@Builder
public record NewEtcSchedule(
    StageType stageType,
    String stageName,
    LocalDateTime scheduledAt,
    String location,
    String link
) { }
