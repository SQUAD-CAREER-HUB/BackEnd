package org.squad.careerhub.domain.application.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;

@Builder
public record NewApplicationInfo(
        ApplicationMethod applicationMethod,
        LocalDateTime deadline
) {

}