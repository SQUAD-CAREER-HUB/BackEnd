package org.squad.careerhub.domain.member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "회원 프로필 조회 응답 DTO (Controller 레이어)")
@Builder
public record MemberProfileResponse(
    @Schema(description = "닉네임", example = "careerhub_dev")
    String nickname,

    @Schema(description = "회원 역할", example = "ROLE_MEMBER")
    String role,

    @Schema(description = "프로필 이미지 url", example = "https://placehold.co/400")
    String profileImageUrl,

    @Schema(
        description = "회원 가입 일시",
        example = "2025-11-30T12:34:56"
    )
    LocalDateTime createdAt
) {

    public static MemberProfileResponse mock() {
        return MemberProfileResponse.builder()
            .nickname("careerhub_dev")
            .role("ROLE_MEMBER")
            .profileImageUrl("https://placehold.co/400")
            .createdAt(LocalDateTime.of(2025, 11, 30, 12, 34, 56))
            .build();
    }
}
