package org.squad.careerhub.domain.auth.service;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.security.jwt.JwtProvider;
import org.squad.careerhub.global.security.jwt.dto.TokenResponse;

@RequiredArgsConstructor
@Transactional
class AuthServiceIntegrationTest extends IntegrationTestSupport {

    final AuthService authService;
    final JwtProvider jwtProvider;
    final MemberJpaRepository memberJpaRepository;
    final EntityManager entityManager;

    @Test
    void 토큰을_재발급을_한다() throws InterruptedException {
        // given
        var member = memberJpaRepository.save(MemberFixture.createMember());
        var oldRefreshToken = jwtProvider.createTokens(member.getId(), member.getRole()).refreshToken();
        member.updateRefreshToken(oldRefreshToken);
        entityManager.flush();

        // 토큰 재발급 시점까지 시간 차이를 두기 위해 1초 대기
        sleep(1000);

        // when
        var tokenResponse = authService.reissue(oldRefreshToken);
        entityManager.flush();
        entityManager.refresh(member);

        // then
        assertThat(tokenResponse).isNotNull()
                .extracting(TokenResponse::accessToken, TokenResponse::refreshToken)
                .doesNotContainNull();
        assertThat(member.getRefreshToken()).isEqualTo(tokenResponse.refreshToken());
        assertThat(oldRefreshToken).isNotEqualTo(tokenResponse.refreshToken());
    }

}