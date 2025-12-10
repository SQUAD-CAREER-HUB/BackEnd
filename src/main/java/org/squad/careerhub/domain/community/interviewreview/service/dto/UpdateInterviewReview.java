package org.squad.careerhub.domain.community.interviewreview.service.dto;

import lombok.Builder;

@Builder
public record UpdateInterviewReview(
        String company,
        String position,
        String interviewType,
        String content
) {

}

