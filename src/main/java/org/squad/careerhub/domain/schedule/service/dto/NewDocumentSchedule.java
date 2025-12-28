package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;

@Builder
public record NewDocumentSchedule(
    StageType stageType,
    LocalDateTime endedAt,
    SubmissionStatus submissionStatus
) { }
