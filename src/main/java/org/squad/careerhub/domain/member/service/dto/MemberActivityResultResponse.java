package org.squad.careerhub.domain.member.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MemberActivityResultResponse(
    Long activityId,
    String type,
    String targetType,
    Long targetId,
    LocalDateTime createdAt
) {

    public static MemberActivityResultResponse of(
        Long activityId,
        String type,
        String targetType,
        Long targetId,
        LocalDateTime createdAt
    ) {
        return MemberActivityResultResponse.builder()
            .activityId(activityId)
            .type(type)
            .targetType(targetType)
            .targetId(targetId)
            .createdAt(createdAt)
            .build();
    }
}