package org.squad.careerhub.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.member.service.dto.MemberProfileResultResponse;

@Schema(description = "회원 프로필 조회 응답 DTO (Controller 레이어)")
@Builder
public record MemberProfileResponse(

    @Schema(description = "회원 ID", example = "1")
    Long memberId,

    @Schema(description = "닉네임", example = "careerhub_dev")
    String nickname,

    @Schema(description = "회원 역할", example = "ROLE_MEMBER")
    String role,

    @Schema(
        description = "회원 가입 일시",
        example = "2025-11-30T12:34:56"
    )
    LocalDateTime createdAt
) {

    public static MemberProfileResponse fromResult(MemberProfileResultResponse result) {
        return MemberProfileResponse.builder()
            .memberId(result.memberId())
            .nickname(result.nickname())
            .build();
    }

    public static MemberProfileResponse mock() {
        return MemberProfileResponse.builder()
            .memberId(1L)
            .nickname("careerhub_dev")
            .role("ROLE_MEMBER")
            .createdAt(LocalDateTime.of(2025, 11, 30, 12, 34, 56))
            .build();
    }
}
