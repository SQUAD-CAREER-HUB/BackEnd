package org.squad.careerhub.domain.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

// NOTE:  파일 이동 예정
@Schema(description = "재발급 토큰 응답 DTO")
@Builder
public record TokenResponse(
        @Schema(description = "accessToken", example = "eyJhbGci...")
        String accessToken,

        @Schema(description = "refreshToken", example = "eyJhbGci...")
        String refreshToken
) {

}