package org.squad.careerhub.domain.schedule.service.dto;

import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;

@Builder
public record NewDocsSchedule(
        SubmissionStatus submissionStatus,
        ScheduleResult scheduleResult
) {

}