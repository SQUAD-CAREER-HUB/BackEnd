package org.squad.careerhub.domain.archive.service.dto;

import lombok.Builder;

@Builder
public record PersonalQuestionCreateParamRequest(
    Long memberId,
    Long applicationId,
    Long interviewQuestionId,
    String question,
    String answer
) {
    public static PersonalQuestionCreateParamRequest of(
        Long memberId,
        Long applicationId,
        Long interviewQuestionId,
        String question,
        String answer
    ) {
        return PersonalQuestionCreateParamRequest.builder()
            .memberId(memberId)
            .applicationId(applicationId)
            .interviewQuestionId(interviewQuestionId)
            .question(question)
            .answer(answer)
            .build();
    }
}
