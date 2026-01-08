package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.ApplicationFixture;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationCreationStatisticsResponse;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;

@RequiredArgsConstructor
@Transactional
class ApplicationCreationStatisticsIntegrationTest extends IntegrationTestSupport {

    final ApplicationReader applicationReader;
    final ApplicationJpaRepository applicationJpaRepository;
    final MemberJpaRepository memberJpaRepository;

    Member testMember;

    @BeforeEach
    void setUp() {
        testMember = memberJpaRepository.save(MemberFixture.createMember());
    }

    @Test
    void 주간_월간_생성된_지원서_통계를_조회한다() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 1, 8, 0, 0); // 2026-01-08 (수요일)

        // 이번 주 (1/6 월 ~ 1/12 일) - 3건
        createApplication(testMember, now.minusDays(2)); // 1/6 월요일
        createApplication(testMember, now.minusDays(1)); // 1/7 화요일
        createApplication(testMember, now); // 1/8 수요일

        // 지난주 (12/30 월 ~ 1/5 일) - 2건
        createApplication(testMember, now.minusWeeks(1)); // 1/1
        createApplication(testMember, now.minusWeeks(1).plusDays(3)); // 1/4

        // 2주 전 - 1건
        createApplication(testMember, now.minusWeeks(2));

        // 이번 달 (2026-01) - 6건 (위의 모든 건)
        // 지난 달 (2025-12) - 2건
        createApplication(testMember, now.minusMonths(1).withDayOfMonth(15)); // 12/15
        createApplication(testMember, now.minusMonths(1).withDayOfMonth(25)); // 12/25

        // when
        ApplicationCreationStatisticsResponse response = applicationReader.getApplicationCreationStatistics(
                testMember.getId(),
                6,  // 6주
                6   // 6개월
        );

        // then
        assertThat(response).isNotNull();
        assertThat(response.weeklyStatistics()).hasSize(6);
        assertThat(response.monthlyStatistics()).hasSize(6);

        // 주간 통계 검증
        List<ApplicationCreationStatisticsResponse.WeeklyStatistics> weeklyStats = response.weeklyStatistics();

        // 이번 주 (제일 마지막, isCurrentWeek = true)
        ApplicationCreationStatisticsResponse.WeeklyStatistics currentWeek = weeklyStats.get(5);
        assertThat(currentWeek.isCurrentWeek()).isTrue();
        assertThat(currentWeek.count()).isGreaterThanOrEqualTo(3); // 최소 3건
        // 2026-01-08(수)이 속한 주: 2026-01-06(월) ~ 2026-01-12(일)
        // 실제로는 DayOfWeek.MONDAY로 계산되므로 월요일이 시작
        assertThat(currentWeek.period()).matches("01\\.\\d{2} - 01\\.\\d{2}"); // 1월 어느 주간

        // 월간 통계 검증
        List<ApplicationCreationStatisticsResponse.MonthlyStatistics> monthlyStats = response.monthlyStatistics();

        // 이번 달 (제일 마지막, isCurrentMonth = true)
        ApplicationCreationStatisticsResponse.MonthlyStatistics currentMonth = monthlyStats.get(5);
        assertThat(currentMonth.isCurrentMonth()).isTrue();
        assertThat(currentMonth.count()).isGreaterThanOrEqualTo(6);
        assertThat(currentMonth.period()).isEqualTo("2026.01");
    }

    @Test
    void 생성된_지원서가_없을_때_모든_통계가_0이다() {
        // when
        ApplicationCreationStatisticsResponse response = applicationReader.getApplicationCreationStatistics(
                testMember.getId(),
                6,  // 6주
                6   // 6개월
        );

        // then
        assertThat(response.weeklyStatistics()).hasSize(6);
        assertThat(response.monthlyStatistics()).hasSize(6);

        // 모든 주간 통계가 0
        assertThat(response.weeklyStatistics())
                .allMatch(stat -> stat.count() == 0);

        // 모든 월간 통계가 0
        assertThat(response.monthlyStatistics())
                .allMatch(stat -> stat.count() == 0);
    }

    @Test
    void 커스텀_기간_4주_3개월_으로_통계를_조회한다() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 1, 8, 0, 0);

        // 최근 4주간 데이터 생성
        for (int i = 0; i < 4; i++) {
            createApplication(testMember, now.minusWeeks(i));
        }

        // 최근 3개월간 데이터 생성
        for (int i = 0; i < 3; i++) {
            createApplication(testMember, now.minusMonths(i).withDayOfMonth(15));
        }

        // when - 4주, 3개월 요청
        ApplicationCreationStatisticsResponse response = applicationReader.getApplicationCreationStatistics(
                testMember.getId(),
                4,  // 4주
                3   // 3개월
        );

        // then
        assertThat(response.weeklyStatistics()).hasSize(4);
        assertThat(response.monthlyStatistics()).hasSize(3);

        // 이번 주/이번 달이 마지막
        assertThat(response.weeklyStatistics().get(3).isCurrentWeek()).isTrue();
        assertThat(response.monthlyStatistics().get(2).isCurrentMonth()).isTrue();
    }

    @Test
    void 최대_기간_12주_12개월_으로_통계를_조회한다() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 1, 8, 0, 0);

        // when
        ApplicationCreationStatisticsResponse response = applicationReader.getApplicationCreationStatistics(
                testMember.getId(),
                12,  // 1년
                12   // 2년
        );

        // then
        assertThat(response.weeklyStatistics()).hasSize(12);
        assertThat(response.monthlyStatistics()).hasSize(12);

        // 모든 통계가 0 (데이터 없음)
        assertThat(response.weeklyStatistics())
                .allMatch(stat -> stat.count() == 0);
        assertThat(response.monthlyStatistics())
                .allMatch(stat -> stat.count() == 0);
    }

    @Test
    void 최소_기간_1주_1개월_으로_통계를_조회한다() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 1, 8, 0, 0);
        createApplication(testMember, now);
        createApplication(testMember, now.minusDays(1));

        // when
        ApplicationCreationStatisticsResponse response = applicationReader.getApplicationCreationStatistics(
                testMember.getId(),
                1,  // 이번 주만
                1   // 이번 달만
        );

        // then
        assertThat(response.weeklyStatistics()).hasSize(1);
        assertThat(response.monthlyStatistics()).hasSize(1);

        assertThat(response.weeklyStatistics().getFirst().isCurrentWeek()).isTrue();
        assertThat(response.weeklyStatistics().getFirst().count()).isEqualTo(2);

        assertThat(response.monthlyStatistics().getFirst().isCurrentMonth()).isTrue();
        assertThat(response.monthlyStatistics().getFirst().count()).isEqualTo(2);
    }

    private void createApplication(Member member, LocalDateTime createdAt) {
        Application application = ApplicationFixture.createApplicationDocs(member);

        // createdAt 설정을 위해 리플렉션 사용
        try {
            var field = application.getClass().getSuperclass().getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(application, createdAt);
        } catch (Exception e) {
            // 테스트 환경에서만 사용
        }

        applicationJpaRepository.save(application);
    }
}

