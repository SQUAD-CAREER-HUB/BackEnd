package org.squad.careerhub.domain.auth.controller;

import static org.squad.careerhub.global.error.ErrorStatus.FORBIDDEN_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.INTERNAL_SERVER_ERROR;
import static org.squad.careerhub.global.error.ErrorStatus.INVALID_TOKEN;
import static org.squad.careerhub.global.error.ErrorStatus.NOT_FOUND_TOKEN;
import static org.squad.careerhub.global.error.ErrorStatus.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.squad.careerhub.domain.auth.controller.dto.ReissueTokenRequest;
import org.squad.careerhub.domain.auth.controller.dto.TokenResponse;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Auth", description = "인증 관련 API 문서")
public abstract class AuthDocsController {

    @Operation(
            summary = "Access, Refresh Token 재발급 요청 - JWT O",
            description = "Refresh 토큰을 이용해 새로운 Access Token 및 Refresh Token을 재발급합니다."
    )
    @RequestBody(
            description = "토큰 재발급 요청",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ReissueTokenRequest.class)
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Access, Refresh Token 재발급 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TokenResponse.class)
            )
    )
    @ApiExceptions(values = {
            INVALID_TOKEN,
            NOT_FOUND_TOKEN,
            INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<TokenResponse> reissue(ReissueTokenRequest request);

    @Operation(summary = "KAKAO 소셜 로그인 - JWT X",
            description = """
                    KAKAO 소셜 로그인 인증 성공 시 쿼리 파라미터로 accessToken, refreshToken이 전달됩니다.<br>
                    <ul>
                        <li><b>url 예시</<b>: http://localhost:3000/?accessToken=...&refreshToken=...</li>
                    </ul>
                    """
    )
    @ApiResponse(
            responseCode = "302",
            description = """
                    카카오 로그인 성공 후 리다이렉트 되며 쿼리 파라미터로 accessToken, refreshToken이 전달됩니다.
                    """
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            FORBIDDEN_ERROR,
            INTERNAL_SERVER_ERROR
    })
    @GetMapping("/oauth2/authorization/kakao")
    public void kakao(){}

    @Operation(summary = "GOOGLE 소셜 로그인 - JWT X",
            description = """
                    GOOGLE 소셜 로그인 인증 성공 시 쿼리 파라미터로 accessToken, refreshToken이 전달됩니다.<br>
                    <ul>
                        <li><b>url 예시</<b>: http://localhost:3000/?accessToken=...&refreshToken=...</li>
                    </ul>
                    """
    )
    @ApiResponse(
            responseCode = "302",
            description = """
                   GOOGLE 소셜 로그인 성공 후 리다이렉트 되며 쿼리 파라미터로 accessToken, refreshToken이 전달됩니다.
                   """
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            FORBIDDEN_ERROR,
            INTERNAL_SERVER_ERROR
    })
    @GetMapping("/oauth2/authorization/google")
    public void google(){}

}