package org.squad.careerhub.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.security.jwt.dto.TokenResponse;

@RequiredArgsConstructor
class AuthServiceIntegrationTest extends IntegrationTestSupport {

    final AuthService authService;
    final MemberJpaRepository memberJpaRepository;
    final EntityManager entityManager;

    Member testMember;
    String initialRefreshToken;

    @BeforeEach
    void setUp() {
        testMember = MemberFixture.createMember();
        testMember.updateRefreshToken("initial");

        memberJpaRepository.save(testMember);
        initialRefreshToken = testMember.getRefreshToken();
    }

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAll();
    }


    @Transactional
    @Test
    void 토큰을_재발급을_한다() {
        // when
        var tokenResponse = authService.reissue(initialRefreshToken);
        entityManager.flush();
        entityManager.refresh(testMember);

        // then
        assertThat(tokenResponse).isNotNull()
                .extracting(TokenResponse::accessToken, TokenResponse::refreshToken)
                .doesNotContainNull();
        assertThat(testMember.getRefreshToken()).isEqualTo(tokenResponse.refreshToken());
        assertThat(initialRefreshToken).isNotEqualTo(tokenResponse.refreshToken());

        memberJpaRepository.deleteById(testMember.getId());
    }

    @Test
    void 존재하지_않는_RT로_재발급은_불가능하다() {
        // given
        var invalidRefreshToken = "invalidRefreshToken";

        // when & then
        assertThatThrownBy(() -> authService.reissue(invalidRefreshToken))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_ACTIVE_MEMBER_BY_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("동시에 같은 Refresh Token 으로 재발급 요청 시 하나만 성공하고 나머지는 실패해야 한다")
    void testConcurrentReissueWithSameRefreshToken() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    authService.reissue(initialRefreshToken);
                    successCount.incrementAndGet();
                } catch (CareerHubException e) {
                    if (e.getErrorStatus() == ErrorStatus.NOT_FOUND_ACTIVE_MEMBER_BY_REFRESH_TOKEN
                            || e.getErrorStatus() == ErrorStatus.CONCURRENT_REQUESTS_LIMIT_EXCEEDED
                    ) {
                        failCount.incrementAndGet();
                    }
                    exceptions.add(e);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        assertThat(failCount.get()).isEqualTo(threadCount - 1);
        assertThat(successCount.get()).isEqualTo(1);

        // 성공한 요청으로 인해 Refresh Token이 변경되었는지 확인
        Member updatedMember = memberJpaRepository.findById(testMember.getId()).orElseThrow();
        assertThat(updatedMember.getRefreshToken()).isNotEqualTo(initialRefreshToken);
    }
}