package org.squad.careerhub.domain.community.interviewquestion.service.dto;

import lombok.Builder;

@Builder
public record UpdateReviewQuestion(
        Long id,
        String question
) {

}