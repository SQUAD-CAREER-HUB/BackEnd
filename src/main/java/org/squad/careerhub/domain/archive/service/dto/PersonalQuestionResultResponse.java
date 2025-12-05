package org.squad.careerhub.domain.archive.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PersonalQuestionResultResponse(
    Long id,
    Long applicationId,
    Long interviewQuestionId,
    String question,
    String answer,
    boolean fromCommunity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PersonalQuestionResultResponse of(
        Long id,
        Long applicationId,
        Long interviewQuestionId,
        String question,
        String answer,
        boolean fromCommunity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return PersonalQuestionResultResponse.builder()
            .id(id)
            .applicationId(applicationId)
            .interviewQuestionId(interviewQuestionId)
            .question(question)
            .answer(answer)
            .fromCommunity(fromCommunity)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }

}
