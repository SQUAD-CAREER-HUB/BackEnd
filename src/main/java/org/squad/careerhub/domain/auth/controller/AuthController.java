package org.squad.careerhub.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.auth.controller.dto.ReissueTokenRequest;
import org.squad.careerhub.global.security.jwt.dto.TokenResponse;

@RequiredArgsConstructor
@RestController
public class AuthController extends AuthDocsController {

    @Override
    @PostMapping("/v1/auth/reissue")
    public ResponseEntity<TokenResponse> reissue(@Valid @RequestBody ReissueTokenRequest request) {
        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .build()
        );
    }

    @GetMapping("/oauth/success")
    public String oauthSuccess(
            @RequestParam String accessToken,
            @RequestParam String refreshToken
    ) {
        return String.format("""
        <html>
        <body>
            <h2>✅ 로그인 성공!</h2>
            <p><strong>Access Token:</strong><br>%s</p>
            <p><strong>Refresh Token:</strong><br>%s</p>
        </body>
        </html>
        """, accessToken, refreshToken);
    }

    @GetMapping("/oauth/fail")
    public String oauthFail() {
        return """
        <html>
        <body>
            <h2>✅ 로그인 실패!</h2>
        </body>
        </html>
        """;
    }

}