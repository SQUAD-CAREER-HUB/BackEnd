package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InterviewScheduleInfo(
    Long id,
    Long applicationId,
    String company,
    String name,
    String type,
    LocalDateTime datetime,
    String location,
    String onlineLink,
    String status
) {

}

