package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.repository.InterviewType;

@Builder
public record InterviewScheduleCreateCommand(
    Long memberId,
    Long applicationId,
    String name,
    InterviewType type,
    LocalDateTime datetime,
    String location,
    String onlineLink
) {
    public static InterviewScheduleCreateCommand of(
        Long memberId,
        Long applicationId,
        String name,
        InterviewType type,
        LocalDateTime datetime,
        String location,
        String onlineLink
    ) {
        return new InterviewScheduleCreateCommand(
            memberId,
            applicationId,
            name,
            type,
            datetime,
            location,
            onlineLink
        );
    }
}
