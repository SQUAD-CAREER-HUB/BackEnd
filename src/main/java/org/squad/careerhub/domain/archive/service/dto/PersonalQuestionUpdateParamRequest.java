package org.squad.careerhub.domain.archive.service.dto;

import lombok.Builder;

@Builder
public record PersonalQuestionUpdateParamRequest(Long memberId,
    Long applicationId,
    Long personalQuestionId,
    String question,
    String answer
) {

    public static PersonalQuestionUpdateParamRequest of(
        Long memberId,
        Long applicationId,
        Long personalQuestionId,
        String question,
        String answer
    ) {
        return PersonalQuestionUpdateParamRequest.builder()
            .memberId(memberId)
            .applicationId(applicationId)
            .personalQuestionId(personalQuestionId)
            .question(question)
            .answer(answer)
            .build();
    }
}
