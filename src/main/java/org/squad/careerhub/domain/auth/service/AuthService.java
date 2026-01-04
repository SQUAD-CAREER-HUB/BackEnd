package org.squad.careerhub.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.service.MemberReader;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.security.jwt.JwtProvider;
import org.squad.careerhub.global.security.jwt.dto.TokenResponse;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final MemberReader memberReader;

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.isTokenValid(refreshToken)) {
            throw new CareerHubException(ErrorStatus.INVALID_TOKEN);
        }

        Member member = memberReader.findByRefreshToken(refreshToken);
        TokenResponse response = jwtProvider.createTokens(member.getId(), member.getRole());
        member.updateRefreshToken(response.refreshToken());

        return response;
    }

}