package org.squad.careerhub.domain.application.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ApplicationStageTest {

    @Test
    void create로_서류_전형_ApplicationStage를_생성한다() {
        // given
        Application application = mock(Application.class);
        StageType stageType = StageType.DOCUMENT;
        String stageName = "서류 전형";
        SubmissionStatus submissionStatus = SubmissionStatus.NOT_SUBMITTED;

        // when
        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                stageType,
                stageName,
                submissionStatus
        );

        // then
        assertThat(applicationStage).isNotNull();
        assertThat(applicationStage.getApplication()).isEqualTo(application);
        assertThat(applicationStage.getStageType()).isEqualTo(stageType);
        assertThat(applicationStage.getStageName()).isEqualTo(stageName);
        assertThat(applicationStage.getStageStatus()).isEqualTo(StageStatus.WAITING);
        assertThat(applicationStage.getSubmissionStatus()).isEqualTo(submissionStatus);
    }

    @Test
    void create로_면접_전형_ApplicationStage를_생성하면_submissionStatus는_null이다() {
        // given
        Application application = mock(Application.class);
        StageType stageType = StageType.INTERVIEW;
        String stageName = "면접 전형";

        // when
        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                stageType,
                stageName,
                null
        );

        // then
        assertThat(applicationStage).isNotNull();
        assertThat(applicationStage.getStageType()).isEqualTo(stageType);
        assertThat(applicationStage.getSubmissionStatus()).isNull();
    }

    @Test
    void create로_기타_전형_ApplicationStage를_생성하면_submissionStatus는_null이다() {
        // given
        Application application = mock(Application.class);
        StageType stageType = StageType.ETC;
        String stageName = "코딩 테스트";

        // when
        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                stageType,
                stageName,
                null
        );

        // then
        assertThat(applicationStage).isNotNull();
        assertThat(applicationStage.getStageType()).isEqualTo(stageType);
        assertThat(applicationStage.getStageName()).isEqualTo(stageName);
        assertThat(applicationStage.getSubmissionStatus()).isNull();
    }

    @Test
    void createPassedDocumentStage로_통과한_서류_전형을_생성한다() {
        // given
        Application application = mock(Application.class);

        // when
        ApplicationStage documentStage = ApplicationStage.createPassedDocumentStage(application);

        // then
        assertThat(documentStage).isNotNull();
        assertThat(documentStage.getApplication()).isEqualTo(application);
        assertThat(documentStage.getStageType()).isEqualTo(StageType.DOCUMENT);
        assertThat(documentStage.getStageName()).isEqualTo(StageType.DOCUMENT.getDescription());
        assertThat(documentStage.getStageStatus()).isEqualTo(StageStatus.PASS);
        assertThat(documentStage.getSubmissionStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
    }

    @Test
    void 서류_전형이_아닌_경우_submissionStatus를_전달해도_null로_설정된다() {
        // given
        Application application = mock(Application.class);
        StageType stageType = StageType.INTERVIEW;
        SubmissionStatus submissionStatus = SubmissionStatus.SUBMITTED; // 전달했지만 무시되어야 함

        // when
        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                stageType,
                "면접 전형",
                submissionStatus
        );

        // then
        assertThat(applicationStage.getSubmissionStatus()).isNull();
    }

    @Test
    void 서류_전형_생성_시_stageStatus는_항상_WAITING이다() {
        // given
        Application application = mock(Application.class);

        // when
        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                StageType.DOCUMENT,
                "서류 전형",
                SubmissionStatus.NOT_SUBMITTED
        );

        // then
        assertThat(applicationStage.getStageStatus()).isEqualTo(StageStatus.WAITING);
    }

    @ParameterizedTest
    @EnumSource(value = StageType.class, names = {"INTERVIEW", "ETC", "FINAL_PASS", "FINAL_FAIL"})
    void 서류_외_모든_전형은_submissionStatus가_null이다(StageType stageType) {
        // given
        Application application = mock(Application.class);
        String stageName = stageType.getDescription();

        // when
        ApplicationStage applicationStage = ApplicationStage.create(
                application,
                stageType,
                stageName,
                SubmissionStatus.SUBMITTED // 전달해도 무시됨
        );

        // then
        assertThat(applicationStage.getSubmissionStatus()).isNull();
    }

    @Test
    void 최종합격_전형_ApplicationStage를_생성한다() {
        // given
        Application application = mock(Application.class);

        // when
        ApplicationStage finalStage = ApplicationStage.create(
                application,
                StageType.FINAL_PASS,
                StageType.FINAL_PASS.getDescription(),
                null
        );

        // then
        assertThat(finalStage.getStageType()).isEqualTo(StageType.FINAL_PASS);
        assertThat(finalStage.getStageStatus()).isEqualTo(StageStatus.WAITING);
    }

    @Test
    void 최종불합격_전형_ApplicationStage를_생성한다() {
        // given
        Application application = mock(Application.class);

        // when
        ApplicationStage finalStage = ApplicationStage.create(
                application,
                StageType.FINAL_FAIL,
                StageType.FINAL_FAIL.getDescription(),
                null
        );

        // then
        assertThat(finalStage.getStageType()).isEqualTo(StageType.FINAL_FAIL);
        assertThat(finalStage.getStageStatus()).isEqualTo(StageStatus.WAITING);
    }

    @ParameterizedTest
    @EnumSource(SubmissionStatus.class)
    void 서류_전형은_모든_SubmissionStatus를_가질_수_있다(SubmissionStatus submissionStatus) {
        // given
        Application application = mock(Application.class);

        // when
        ApplicationStage documentStage = ApplicationStage.create(
                application,
                StageType.DOCUMENT,
                "서류 전형",
                submissionStatus
        );

        // then
        assertThat(documentStage.getSubmissionStatus()).isEqualTo(submissionStatus);
    }

}