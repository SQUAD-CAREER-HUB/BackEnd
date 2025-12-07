package org.squad.careerhub.domain.archive.service.dto;

import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.archive.controller.dto.PersonalQuestionResponse;

@Builder
public record PersonalQuestionPageResultResponse(
    List<PersonalQuestionPageResultResponse> questions,
    boolean hasNext,
    Long nextCursorId
) {
    public static PersonalQuestionPageResultResponse of(
        List<PersonalQuestionPageResultResponse> questions,
        boolean hasNext,
        Long nextCursorId
    ) {
        return PersonalQuestionPageResultResponse.builder()
            .questions(questions)
            .hasNext(hasNext)
            .nextCursorId(nextCursorId)
            .build();
    }
}
