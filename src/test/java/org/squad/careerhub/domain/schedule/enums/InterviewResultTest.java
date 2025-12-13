package org.squad.careerhub.domain.schedule.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InterviewResultTest {

    @Test
    void InterviewResult는_정의된_모든_값을_가진다() {
        // when
        InterviewResult[] values = InterviewResult.values();

        // then
        assertThat(values).hasSize(3);
        assertThat(values).containsExactly(
                InterviewResult.PENDING,
                InterviewResult.PASS,
                InterviewResult.FAIL
        );
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, 대기중",
            "PASS, 합격",
            "FAIL, 불합격"
    })
    void 각_InterviewResult는_올바른_설명을_가진다(InterviewResult result, String expectedDescription) {
        // then
        assertThat(result.getDescription()).isEqualTo(expectedDescription);
    }

    @Test
    void PENDING은_대기중_설명을_가진다() {
        // when
        InterviewResult pending = InterviewResult.PENDING;

        // then
        assertThat(pending.getDescription()).isEqualTo("대기중");
    }

    @Test
    void PASS는_합격_설명을_가진다() {
        // when
        InterviewResult pass = InterviewResult.PASS;

        // then
        assertThat(pass.getDescription()).isEqualTo("합격");
    }

    @Test
    void FAIL은_불합격_설명을_가진다() {
        // when
        InterviewResult fail = InterviewResult.FAIL;

        // then
        assertThat(fail.getDescription()).isEqualTo("불합격");
    }

}