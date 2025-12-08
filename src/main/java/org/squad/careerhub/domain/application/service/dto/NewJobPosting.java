package org.squad.careerhub.domain.application.service.dto;

import lombok.Builder;

@Builder
public record NewJobPosting(
        String jobPostingUrl,
        String company,
        String position,
        String jobLocation
) {

}