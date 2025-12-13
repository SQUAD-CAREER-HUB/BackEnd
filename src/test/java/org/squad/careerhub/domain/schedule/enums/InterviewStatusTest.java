package org.squad.careerhub.domain.schedule.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InterviewStatusTest {

    @Test
    void InterviewStatus는_정의된_모든_값을_가진다() {
        // when
        InterviewStatus[] values = InterviewStatus.values();

        // then
        assertThat(values).hasSize(4);
        assertThat(values).containsExactly(
                InterviewStatus.SCHEDULED,
                InterviewStatus.DONE,
                InterviewStatus.CANCELED,
                InterviewStatus.NONAPPEARANCE
        );
    }

    @ParameterizedTest
    @CsvSource({
            "SCHEDULED, 예정",
            "DONE, 진행 완료",
            "CANCELED, 취소",
            "NONAPPEARANCE, 불참"
    })
    void 각_InterviewStatus는_올바른_설명을_가진다(InterviewStatus status, String expectedDescription) {
        // then
        assertThat(status.getDescription()).isEqualTo(expectedDescription);
    }

    @Test
    void SCHEDULED는_예정_설명을_가진다() {
        // when
        InterviewStatus scheduled = InterviewStatus.SCHEDULED;

        // then
        assertThat(scheduled.getDescription()).isEqualTo("예정");
    }

    @Test
    void DONE은_진행완료_설명을_가진다() {
        // when
        InterviewStatus done = InterviewStatus.DONE;

        // then
        assertThat(done.getDescription()).isEqualTo("진행 완료");
    }

    @Test
    void CANCELED는_취소_설명을_가진다() {
        // when
        InterviewStatus canceled = InterviewStatus.CANCELED;

        // then
        assertThat(canceled.getDescription()).isEqualTo("취소");
    }

    @Test
    void NONAPPEARANCE는_불참_설명을_가진다() {
        // when
        InterviewStatus nonappearance = InterviewStatus.NONAPPEARANCE;

        // then
        assertThat(nonappearance.getDescription()).isEqualTo("불참");
    }

}