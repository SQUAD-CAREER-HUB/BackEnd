package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@Builder
public record NewInterviewSchedule(
        String stageName,
        InterviewType type,
        LocalDateTime scheduledAt,
        String location
) {

}