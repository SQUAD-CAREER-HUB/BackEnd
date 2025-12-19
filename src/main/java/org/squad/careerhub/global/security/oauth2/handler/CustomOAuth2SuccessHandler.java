package org.squad.careerhub.global.security.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.squad.careerhub.domain.member.service.MemberManager;
import org.squad.careerhub.global.security.jwt.JwtProvider;
import org.squad.careerhub.global.security.jwt.dto.TokenResponse;
import org.squad.careerhub.global.security.jwt.enums.TokenType;
import org.squad.careerhub.global.security.oauth2.dto.CustomOAuth2Member;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final MemberManager memberManager;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2Member oAuth2Member = (CustomOAuth2Member) authentication.getPrincipal();
        TokenResponse tokenResponse = jwtProvider.createTokens(oAuth2Member.getMemberId(), oAuth2Member.getRole());

        memberManager.updateRefreshToken(oAuth2Member.getMemberId(), tokenResponse.refreshToken());
        log.info("[Auth] 소셜 로그인 성공: {} ", oAuth2Member.getName());

        String redirectUrl = createRedirectUrl(tokenResponse);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String createRedirectUrl(TokenResponse tokenResponse) {
        return UriComponentsBuilder.fromUriString("http://localhost:3000")
                .queryParam(TokenType.ACCESS.getValue(), tokenResponse.accessToken())
                .queryParam(TokenType.REFRESH.getValue(), tokenResponse.refreshToken())
                .build()
                .toUriString();
    }

}