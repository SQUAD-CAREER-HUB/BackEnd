package org.squad.careerhub.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.auth.controller.dto.ReissueTokenRequest;
import org.squad.careerhub.domain.auth.service.AuthService;
import org.squad.careerhub.global.security.jwt.dto.TokenResponse;

@RequiredArgsConstructor
@RestController
public class AuthController extends AuthDocsController {

    private final AuthService authService;

    @Override
    @PostMapping("/v1/auth/reissue")
    public ResponseEntity<TokenResponse> reissue(@Valid @RequestBody ReissueTokenRequest request) {
        TokenResponse response = authService.reissue(request.refreshToken());

        return ResponseEntity.ok(response);
    }

}