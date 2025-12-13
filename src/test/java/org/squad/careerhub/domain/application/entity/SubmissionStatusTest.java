package org.squad.careerhub.domain.application.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SubmissionStatusTest {

    @Test
    void SubmissionStatus는_정의된_모든_값을_가진다() {
        // when
        SubmissionStatus[] values = SubmissionStatus.values();

        // then
        assertThat(values).hasSize(2);
        assertThat(values).containsExactly(
                SubmissionStatus.NOT_SUBMITTED,
                SubmissionStatus.SUBMITTED
        );
    }

    @Test
    void NOT_SUBMITTED_값이_존재한다() {
        // when
        SubmissionStatus notSubmitted = SubmissionStatus.NOT_SUBMITTED;

        // then
        assertThat(notSubmitted).isNotNull();
        assertThat(notSubmitted.name()).isEqualTo("NOT_SUBMITTED");
    }

    @Test
    void SUBMITTED_값이_존재한다() {
        // when
        SubmissionStatus submitted = SubmissionStatus.SUBMITTED;

        // then
        assertThat(submitted).isNotNull();
        assertThat(submitted.name()).isEqualTo("SUBMITTED");
    }

}