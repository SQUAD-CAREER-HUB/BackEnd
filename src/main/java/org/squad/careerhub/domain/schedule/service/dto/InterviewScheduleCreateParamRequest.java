package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@Builder
public record InterviewScheduleCreateParamRequest(
    Long memberId,
    Long applicationId,
    String name,
    InterviewType type,
    LocalDateTime datetime,
    String location,
    String onlineLink
) {
    public static InterviewScheduleCreateParamRequest of(
        Long memberId,
        Long applicationId,
        String name,
        InterviewType type,
        LocalDateTime datetime,
        String location,
        String onlineLink
    ) {
        return new InterviewScheduleCreateParamRequest(
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
