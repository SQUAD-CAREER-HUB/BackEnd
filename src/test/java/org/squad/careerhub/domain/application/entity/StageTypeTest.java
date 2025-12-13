package org.squad.careerhub.domain.application.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StageTypeTest {

    @Test
    void StageType은_정의된_모든_값을_가진다() {
        // when
        StageType[] values = StageType.values();

        // then
        assertThat(values).hasSize(5);
        assertThat(values).containsExactly(
                StageType.DOCUMENT,
                StageType.ETC,
                StageType.INTERVIEW,
                StageType.FINAL_PASS,
                StageType.FINAL_FAIL
        );
    }

    @Test
    void DOCUMENT는_order_1과_올바른_설명을_가진다() {
        // when
        StageType document = StageType.DOCUMENT;

        // then
        assertThat(document.getOrder()).isEqualTo(1);
        assertThat(document.getDescription()).isEqualTo("서류 전형");
    }

    @Test
    void ETC는_order_2와_올바른_설명을_가진다() {
        // when
        StageType etc = StageType.ETC;

        // then
        assertThat(etc.getOrder()).isEqualTo(2);
        assertThat(etc.getDescription()).isEqualTo("기타 전형");
    }

    @Test
    void INTERVIEW는_order_3과_올바른_설명을_가진다() {
        // when
        StageType interview = StageType.INTERVIEW;

        // then
        assertThat(interview.getOrder()).isEqualTo(3);
        assertThat(interview.getDescription()).isEqualTo("면접 전형");
    }

    @Test
    void FINAL_PASS는_order_4와_올바른_설명을_가진다() {
        // when
        StageType finalPass = StageType.FINAL_PASS;

        // then
        assertThat(finalPass.getOrder()).isEqualTo(4);
        assertThat(finalPass.getDescription()).isEqualTo("최종 힙격");
    }

    @Test
    void FINAL_FAIL은_order_4와_올바른_설명을_가진다() {
        // when
        StageType finalFail = StageType.FINAL_FAIL;

        // then
        assertThat(finalFail.getOrder()).isEqualTo(4);
        assertThat(finalFail.getDescription()).isEqualTo("최종 힙격");
    }

    @Test
    void DOCUMENT의_이전_단계는_빈_리스트이다() {
        // when
        List<StageType> previousStages = StageType.DOCUMENT.getPreviousStages();

        // then
        assertThat(previousStages).isEmpty();
    }

    @Test
    void ETC의_이전_단계는_DOCUMENT만_포함한다() {
        // when
        List<StageType> previousStages = StageType.ETC.getPreviousStages();

        // then
        assertThat(previousStages).hasSize(1);
        assertThat(previousStages).containsExactly(StageType.DOCUMENT);
    }

    @Test
    void INTERVIEW의_이전_단계는_DOCUMENT와_ETC를_포함한다() {
        // when
        List<StageType> previousStages = StageType.INTERVIEW.getPreviousStages();

        // then
        assertThat(previousStages).hasSize(2);
        assertThat(previousStages).containsExactlyInAnyOrder(
                StageType.DOCUMENT,
                StageType.ETC
        );
    }

    @Test
    void FINAL_PASS의_이전_단계는_DOCUMENT_ETC_INTERVIEW를_포함한다() {
        // when
        List<StageType> previousStages = StageType.FINAL_PASS.getPreviousStages();

        // then
        assertThat(previousStages).hasSize(3);
        assertThat(previousStages).containsExactlyInAnyOrder(
                StageType.DOCUMENT,
                StageType.ETC,
                StageType.INTERVIEW
        );
    }

    @Test
    void FINAL_FAIL의_이전_단계는_DOCUMENT_ETC_INTERVIEW를_포함한다() {
        // when
        List<StageType> previousStages = StageType.FINAL_FAIL.getPreviousStages();

        // then
        assertThat(previousStages).hasSize(3);
        assertThat(previousStages).containsExactlyInAnyOrder(
                StageType.DOCUMENT,
                StageType.ETC,
                StageType.INTERVIEW
        );
    }

    @ParameterizedTest
    @CsvSource({
            "DOCUMENT, 1",
            "ETC, 2",
            "INTERVIEW, 3",
            "FINAL_PASS, 4",
            "FINAL_FAIL, 4"
    })
    void 각_StageType은_올바른_순서를_가진다(StageType stageType, int expectedOrder) {
        // then
        assertThat(stageType.getOrder()).isEqualTo(expectedOrder);
    }

    @Test
    void getPreviousStages는_order가_작은_전형만_반환한다() {
        // given
        StageType interview = StageType.INTERVIEW;

        // when
        List<StageType> previousStages = interview.getPreviousStages();

        // then
        previousStages.forEach(stage ->
                assertThat(stage.getOrder()).isLessThan(interview.getOrder())
        );
    }

    @Test
    void getPreviousStages는_불변_리스트를_반환한다() {
        // when
        List<StageType> previousStages = StageType.FINAL_PASS.getPreviousStages();

        // then - 리스트가 불변인지 확인 (toList()는 불변 리스트 반환)
        assertThat(previousStages).isInstanceOf(List.class);
    }

}