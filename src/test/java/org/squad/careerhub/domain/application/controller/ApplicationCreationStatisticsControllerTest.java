package org.squad.careerhub.domain.application.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationCreationStatisticsResponse;
import org.squad.careerhub.global.annotation.TestMember;

class ApplicationCreationStatisticsControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 주간_월간_생성_통계를_기본값_6주_6개월_으로_조회한다() {
        // given
        ApplicationCreationStatisticsResponse response = createMockResponse(6, 6);
        given(applicationService.getApplicationCreationStatistics(any(), any(), any()))
                .willReturn(response);

        // when & then
        mvcTester.get().uri("/v1/applications/statistics/creation")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ApplicationCreationStatisticsResponse.class)
                .satisfies(result -> {
                    assertThat(result.weeklyStatistics()).hasSize(6);
                    assertThat(result.weeklyStatistics().get(5).isCurrentWeek()).isTrue();
                    assertThat(result.monthlyStatistics()).hasSize(6);
                    assertThat(result.monthlyStatistics().get(5).isCurrentMonth()).isTrue();
                });
    }

    @TestMember
    @Test
    void 주간_통계를_커스텀_4주_으로_조회한다() {
        // given
        ApplicationCreationStatisticsResponse response = createMockResponse(4, 6);
        given(applicationService.getApplicationCreationStatistics(any(), eq(4), any()))
                .willReturn(response);

        // when & then
        mvcTester.get().uri("/v1/applications/statistics/creation?weekCount=4")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ApplicationCreationStatisticsResponse.class)
                .satisfies(result -> {
                    assertThat(result.weeklyStatistics()).hasSize(4);
                    assertThat(result.weeklyStatistics().get(3).isCurrentWeek()).isTrue();
                    assertThat(result.monthlyStatistics()).hasSize(6);
                });
    }

    @TestMember
    @Test
    void 월간_통계를_커스텀_12개월_으로_조회한다() {
        // given
        ApplicationCreationStatisticsResponse response = createMockResponse(6, 12);
        given(applicationService.getApplicationCreationStatistics(any(), any(), eq(12)))
                .willReturn(response);

        // when & then
        mvcTester.get().uri("/v1/applications/statistics/creation?monthCount=12")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ApplicationCreationStatisticsResponse.class)
                .satisfies(result -> {
                    assertThat(result.weeklyStatistics()).hasSize(6);
                    assertThat(result.monthlyStatistics()).hasSize(12);
                    assertThat(result.monthlyStatistics().get(11).isCurrentMonth()).isTrue();
                });
    }

    @TestMember
    @Test
    void 주간_월간_통계를_모두_커스텀_8주_3개월_으로_조회한다() {
        // given
        ApplicationCreationStatisticsResponse response = createMockResponse(8, 3);
        given(applicationService.getApplicationCreationStatistics(any(), eq(8), eq(3)))
                .willReturn(response);

        // when & then
        mvcTester.get().uri("/v1/applications/statistics/creation?weekCount=8&monthCount=3")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ApplicationCreationStatisticsResponse.class)
                .satisfies(result -> {
                    assertThat(result.weeklyStatistics()).hasSize(8);
                    assertThat(result.weeklyStatistics().get(7).isCurrentWeek()).isTrue();
                    assertThat(result.monthlyStatistics()).hasSize(3);
                    assertThat(result.monthlyStatistics().get(2).isCurrentMonth()).isTrue();
                });
    }

    @TestMember
    @Test
    void 최소_기간_1주_1개월_으로_통계를_조회한다() {
        // given
        ApplicationCreationStatisticsResponse response = createMockResponse(1, 1);
        given(applicationService.getApplicationCreationStatistics(any(), eq(1), eq(1)))
                .willReturn(response);

        // when & then
        mvcTester.get().uri("/v1/applications/statistics/creation?weekCount=1&monthCount=1")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ApplicationCreationStatisticsResponse.class)
                .satisfies(result -> {
                    assertThat(result.weeklyStatistics()).hasSize(1);
                    assertThat(result.weeklyStatistics().getFirst().isCurrentWeek()).isTrue();
                    assertThat(result.monthlyStatistics()).hasSize(1);
                    assertThat(result.monthlyStatistics().getFirst().isCurrentMonth()).isTrue();
                });
    }

    @TestMember
    @Test
    void 최대_기간_12주_12개월_으로_통계를_조회한다() {
        // given
        ApplicationCreationStatisticsResponse response = createMockResponse(12, 12);
        given(applicationService.getApplicationCreationStatistics(any(), eq(12), eq(12)))
                .willReturn(response);

        // when & then
        mvcTester.get().uri("/v1/applications/statistics/creation?weekCount=12&monthCount=12")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ApplicationCreationStatisticsResponse.class)
                .satisfies(result -> {
                    assertThat(result.weeklyStatistics()).hasSize(12);
                    assertThat(result.weeklyStatistics().get(11).isCurrentWeek()).isTrue();
                    assertThat(result.monthlyStatistics()).hasSize(12);
                    assertThat(result.monthlyStatistics().get(11).isCurrentMonth()).isTrue();
                });
    }

    @TestMember
    @Test
    void 통계_조회_시_각_기간의_카운트와_기간_정보를_확인한다() {
        // given
        List<ApplicationCreationStatisticsResponse.WeeklyStatistics> weeklyStats = List.of(
                ApplicationCreationStatisticsResponse.WeeklyStatistics.builder()
                        .period("12.08 - 12.14")
                        .count(3)
                        .isCurrentWeek(false)
                        .build(),
                ApplicationCreationStatisticsResponse.WeeklyStatistics.builder()
                        .period("01.05 - 01.11")
                        .count(10)
                        .isCurrentWeek(true)
                        .build()
        );

        List<ApplicationCreationStatisticsResponse.MonthlyStatistics> monthlyStats = List.of(
                ApplicationCreationStatisticsResponse.MonthlyStatistics.builder()
                        .period("2025.12")
                        .count(22)
                        .isCurrentMonth(false)
                        .build(),
                ApplicationCreationStatisticsResponse.MonthlyStatistics.builder()
                        .period("2026.01")
                        .count(30)
                        .isCurrentMonth(true)
                        .build()
        );

        ApplicationCreationStatisticsResponse response = ApplicationCreationStatisticsResponse.builder()
                .weeklyStatistics(weeklyStats)
                .monthlyStatistics(monthlyStats)
                .build();

        given(applicationService.getApplicationCreationStatistics(any(), any(), any()))
                .willReturn(response);

        // when & then
        mvcTester.get().uri("/v1/applications/statistics/creation")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ApplicationCreationStatisticsResponse.class)
                .satisfies(result -> {
                    // 주간 통계 검증
                    assertThat(result.weeklyStatistics()).hasSize(2);
                    assertThat(result.weeklyStatistics().getFirst().period()).isEqualTo("12.08 - 12.14");
                    assertThat(result.weeklyStatistics().get(0).count()).isEqualTo(3);
                    assertThat(result.weeklyStatistics().get(0).isCurrentWeek()).isFalse();
                    assertThat(result.weeklyStatistics().get(1).period()).isEqualTo("01.05 - 01.11");
                    assertThat(result.weeklyStatistics().get(1).count()).isEqualTo(10);
                    assertThat(result.weeklyStatistics().get(1).isCurrentWeek()).isTrue();

                    // 월간 통계 검증
                    assertThat(result.monthlyStatistics()).hasSize(2);
                    assertThat(result.monthlyStatistics().getFirst().period()).isEqualTo("2025.12");
                    assertThat(result.monthlyStatistics().get(0).count()).isEqualTo(22);
                    assertThat(result.monthlyStatistics().get(0).isCurrentMonth()).isFalse();
                    assertThat(result.monthlyStatistics().get(1).period()).isEqualTo("2026.01");
                    assertThat(result.monthlyStatistics().get(1).count()).isEqualTo(30);
                    assertThat(result.monthlyStatistics().get(1).isCurrentMonth()).isTrue();
                });
    }

    // Helper methods
    private ApplicationCreationStatisticsResponse createMockResponse(int weekCount, int monthCount) {
        List<ApplicationCreationStatisticsResponse.WeeklyStatistics> weeklyStats =
                createWeeklyStats(weekCount);
        List<ApplicationCreationStatisticsResponse.MonthlyStatistics> monthlyStats =
                createMonthlyStats(monthCount);

        return ApplicationCreationStatisticsResponse.builder()
                .weeklyStatistics(weeklyStats)
                .monthlyStatistics(monthlyStats)
                .build();
    }

    private List<ApplicationCreationStatisticsResponse.WeeklyStatistics> createWeeklyStats(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> ApplicationCreationStatisticsResponse.WeeklyStatistics.builder()
                        .period(String.format("12.%02d - 12.%02d", i * 7 + 1, i * 7 + 7))
                        .count(i * 2)
                        .isCurrentWeek(i == count - 1)
                        .build())
                .toList();
    }

    private List<ApplicationCreationStatisticsResponse.MonthlyStatistics> createMonthlyStats(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> ApplicationCreationStatisticsResponse.MonthlyStatistics.builder()
                        .period(String.format("2025.%02d", 12 - count + i + 1))
                        .count(i * 5)
                        .isCurrentMonth(i == count - 1)
                        .build())
                .toList();
    }

}