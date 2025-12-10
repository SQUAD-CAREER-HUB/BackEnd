package org.squad.careerhub.domain.application.service.dto;

import java.time.LocalDate;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;

@Builder
public record NewApplicationInfo(
        ApplicationStatus applicationStatus,
        ApplicationMethod applicationMethod,
        String memo,
        LocalDate deadline,
        LocalDate submittedAt
) {

}