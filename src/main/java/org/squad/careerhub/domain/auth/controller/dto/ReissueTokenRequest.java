package org.squad.careerhub.domain.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청 DTO")
public record ReissueTokenRequest(
        @Schema(description = "refreshToken", example = "eyJhbGci...")
        @NotBlank(message = "리프레시 토큰은 필수 값 입니다.")
        String refreshToken
) {

}