package org.squad.careerhub.domain.schedule.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InterviewTypeTest {

    @Test
    void InterviewType은_정의된_모든_값을_가진다() {
        // when
        InterviewType[] values = InterviewType.values();

        // then
        assertThat(values).hasSize(6);
        assertThat(values).containsExactly(
                InterviewType.TECH,
                InterviewType.FIT,
                InterviewType.EXECUTIVE,
                InterviewType.DESIGN,
                InterviewType.TEST,
                InterviewType.OTHER
        );
    }

    @ParameterizedTest
    @CsvSource({
            "TECH, 기술 면접",
            "FIT, 컬처핏/인성 면접",
            "EXECUTIVE, 임원 면접",
            "DESIGN, 시스템 디자인 면접",
            "TEST, 라이브 코딩 테스트 면접",
            "OTHER, 기타"
    })
    void 각_InterviewType은_올바른_설명을_가진다(InterviewType type, String expectedDescription) {
        // then
        assertThat(type.getDescription()).isEqualTo(expectedDescription);
    }

    @Test
    void TECH는_기술_면접_설명을_가진다() {
        // when
        InterviewType tech = InterviewType.TECH;

        // then
        assertThat(tech.getDescription()).isEqualTo("기술 면접");
    }

    @Test
    void FIT은_컬처핏_인성_면접_설명을_가진다() {
        // when
        InterviewType fit = InterviewType.FIT;

        // then
        assertThat(fit.getDescription()).isEqualTo("컬처핏/인성 면접");
    }

    @Test
    void EXECUTIVE는_임원_면접_설명을_가진다() {
        // when
        InterviewType executive = InterviewType.EXECUTIVE;

        // then
        assertThat(executive.getDescription()).isEqualTo("임원 면접");
    }

    @Test
    void DESIGN은_시스템_디자인_면접_설명을_가진다() {
        // when
        InterviewType design = InterviewType.DESIGN;

        // then
        assertThat(design.getDescription()).isEqualTo("시스템 디자인 면접");
    }

    @Test
    void TEST는_라이브_코딩_테스트_면접_설명을_가진다() {
        // when
        InterviewType test = InterviewType.TEST;

        // then
        assertThat(test.getDescription()).isEqualTo("라이브 코딩 테스트 면접");
    }

    @Test
    void OTHER는_기타_설명을_가진다() {
        // when
        InterviewType other = InterviewType.OTHER;

        // then
        assertThat(other.getDescription()).isEqualTo("기타");
    }

}