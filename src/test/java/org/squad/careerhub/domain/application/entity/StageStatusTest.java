package org.squad.careerhub.domain.application.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StageStatusTest {

    @Test
    void StageStatus는_정의된_모든_값을_가진다() {
        // when
        StageStatus[] values = StageStatus.values();

        // then
        assertThat(values).hasSize(3);
        assertThat(values).containsExactly(
                StageStatus.WAITING,
                StageStatus.PASS,
                StageStatus.FAIL
        );
    }

    @Test
    void WAITING_값이_존재한다() {
        // when
        StageStatus waiting = StageStatus.WAITING;

        // then
        assertThat(waiting).isNotNull();
        assertThat(waiting.name()).isEqualTo("WAITING");
    }

    @Test
    void PASS_값이_존재한다() {
        // when
        StageStatus pass = StageStatus.PASS;

        // then
        assertThat(pass).isNotNull();
        assertThat(pass.name()).isEqualTo("PASS");
    }

    @Test
    void FAIL_값이_존재한다() {
        // when
        StageStatus fail = StageStatus.FAIL;

        // then
        assertThat(fail).isNotNull();
        assertThat(fail.name()).isEqualTo("FAIL");
    }

}