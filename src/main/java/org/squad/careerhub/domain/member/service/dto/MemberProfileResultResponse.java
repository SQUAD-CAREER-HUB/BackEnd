package org.squad.careerhub.domain.member.service.dto;

import lombok.Builder;

@Builder
public record MemberProfileResultResponse(
    Long memberId,
    String nickname,
    String profileImageUrl
) {

    public static MemberProfileResultResponse of(
        Long memberId,
        String nickname,
        String profileImageUrl
    ) {
        return MemberProfileResultResponse.builder()
            .memberId(memberId)
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
            .build();
    }
}
