package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

public record InterviewScheduleUpdateParamRequest(
    String name,
    InterviewType type,
    LocalDateTime datetime,
    String location,
    String onlineLink
) {
    public static InterviewScheduleUpdateParamRequest of(
        String name,
        InterviewType type,
        LocalDateTime datetime,
        String location,
        String onlineLink
    ) {
        return new InterviewScheduleUpdateParamRequest(
            name,
            type,
            datetime,
            location,
            onlineLink
        );
    }
}
