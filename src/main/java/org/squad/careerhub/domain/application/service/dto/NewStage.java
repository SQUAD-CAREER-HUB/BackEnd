package org.squad.careerhub.domain.application.service.dto;

import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

@Builder
public record NewStage(
        StageType stageType,
        SubmissionStatus submissionStatus,
        ApplicationStatus finalApplicationStatus,
        List<NewEtcSchedule> newEtcSchedules,
        List<NewInterviewSchedule> newInterviewSchedules
) {

}