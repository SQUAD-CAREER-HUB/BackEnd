package org.squad.careerhub.domain.application.controller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;

class ApplicationInfoRequestTest {

    @Test
    void toNewApplicationInfo로_NewApplicationInfo를_생성한다() {
        // given
        LocalDate deadline = LocalDate.of(2025, 3, 25);
        LocalDate submittedAt = LocalDate.of(2025, 3, 20);
        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(deadline)
                .submittedAt(submittedAt)
                .applicationMethod(ApplicationMethod.HOMEPAGE)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo).isNotNull();
        assertThat(newApplicationInfo.deadline()).isEqualTo(deadline);
        assertThat(newApplicationInfo.submittedAt()).isEqualTo(submittedAt);
        assertThat(newApplicationInfo.applicationMethod()).isEqualTo(ApplicationMethod.HOMEPAGE);
    }

    @Test
    void submittedAt이_null인_경우_NewApplicationInfo를_생성한다() {
        // given - 아직 제출하지 않은 경우
        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(LocalDate.of(2025, 4, 15))
                .submittedAt(null)
                .applicationMethod(ApplicationMethod.EMAIL)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.submittedAt()).isNull();
        assertThat(newApplicationInfo.deadline()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(ApplicationMethod.class)
    void 모든_ApplicationMethod로_NewApplicationInfo를_생성한다(ApplicationMethod method) {
        // given
        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(LocalDate.now().plusDays(7))
                .applicationMethod(method)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.applicationMethod()).isEqualTo(method);
    }

    @Test
    void 제출일이_마감일보다_이전인_경우() {
        // given
        LocalDate deadline = LocalDate.of(2025, 5, 1);
        LocalDate submittedAt = LocalDate.of(2025, 4, 25);

        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(deadline)
                .submittedAt(submittedAt)
                .applicationMethod(ApplicationMethod.SARAMIN)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.submittedAt()).isBefore(newApplicationInfo.deadline());
    }

    @Test
    void 제출일과_마감일이_같은_경우() {
        // given
        LocalDate sameDate = LocalDate.of(2025, 6, 15);

        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(sameDate)
                .submittedAt(sameDate)
                .applicationMethod(ApplicationMethod.JOBKOREA)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.submittedAt()).isEqualTo(newApplicationInfo.deadline());
    }

    @Test
    void 오늘_날짜로_마감일과_제출일을_설정한다() {
        // given
        LocalDate today = LocalDate.now();
        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(today)
                .submittedAt(today)
                .applicationMethod(ApplicationMethod.LINKEDIN)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.deadline()).isEqualTo(today);
        assertThat(newApplicationInfo.submittedAt()).isEqualTo(today);
    }

    @Test
    void 미래_날짜로_마감일을_설정한다() {
        // given
        LocalDate futureDeadline = LocalDate.now().plusMonths(2);
        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(futureDeadline)
                .applicationMethod(ApplicationMethod.HOMEPAGE)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.deadline()).isAfter(LocalDate.now());
    }

    @Test
    void 과거_날짜로_제출일을_설정한다() {
        // given
        LocalDate pastSubmission = LocalDate.now().minusDays(10);
        ApplicationInfoRequest request = ApplicationInfoRequest.builder()
                .deadline(LocalDate.now())
                .submittedAt(pastSubmission)
                .applicationMethod(ApplicationMethod.EMAIL)
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.submittedAt()).isBefore(LocalDate.now());
    }

}