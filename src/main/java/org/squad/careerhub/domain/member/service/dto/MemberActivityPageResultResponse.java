package org.squad.careerhub.domain.member.service.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberActivityPageResultResponse(
    List<MemberActivityResultResponse> activities,
    boolean hasNext,
    Long nextCursorId
) {

    public static MemberActivityPageResultResponse of(
        List<MemberActivityResultResponse> activities,
        boolean hasNext,
        Long nextCursorId
    ) {
        return MemberActivityPageResultResponse.builder()
            .activities(activities)
            .hasNext(hasNext)
            .nextCursorId(nextCursorId)
            .build();
    }
}
