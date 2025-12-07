package org.squad.careerhub.domain.member.service.dto;

import lombok.Builder;

@Builder
public record MemberProfileUpdateParamRequest (
    Long memberId,
    String nickname,
    String profileImageUrl
) {
    public static MemberProfileUpdateParamRequest of(
        Long memberId,
        String nickname,
        String profileImageUrl
    ) {
        return MemberProfileUpdateParamRequest.builder()
            .memberId(memberId)
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
            .build();
    }
}