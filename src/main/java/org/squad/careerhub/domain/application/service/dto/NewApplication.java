package org.squad.careerhub.domain.application.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;

@Builder
public record NewApplication(
        String jobPostingUrl,
        String company,
        String position,
        LocalDateTime deadline,
        String jobLocation,
        StageType stageType,
        ApplicationMethod applicationMethod,
        ApplicationStatus finalApplicationStatus
) {

}