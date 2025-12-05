package org.squad.careerhub.domain.member.service.dto;

import lombok.Builder;

@Builder
public record MemberProfileUpdateParamRequest (
    Long memberId,
    String nickname
) {
    public static MemberProfileUpdateParamRequest of(
        Long memberId,
        String nickname
    ) {
        return MemberProfileUpdateParamRequest.builder()
            .memberId(memberId)
            .nickname(nickname)
            .build();
    }
}