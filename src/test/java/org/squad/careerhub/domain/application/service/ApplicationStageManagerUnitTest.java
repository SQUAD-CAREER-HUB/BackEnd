package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.application.service.dto.NewStage;

class ApplicationStageManagerUnitTest extends TestDoubleSupport {

    @Mock
    private ApplicationStageJpaRepository applicationStageJpaRepository;

    @InjectMocks
    private ApplicationStageManager applicationStageManager;

    private Application testApplication;

    @BeforeEach
    void setUp() {
        testApplication = mock(Application.class);
    }

    @Test
    void 서류_전형만_생성하면_1번만_저장된다() {
        // given
        var documentNewStage = NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .build();
        var documentStage = ApplicationStage.create(
                testApplication,
                StageType.DOCUMENT,
                StageType.DOCUMENT.getDescription(),
                SubmissionStatus.NOT_SUBMITTED
        );
        given(applicationStageJpaRepository.save(any())).willReturn(documentStage);

        // when
        var result = applicationStageManager.create(testApplication, documentNewStage);

        // then
        assertThat(result).extracting(
                ApplicationStage::getStageStatus,
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getSubmissionStatus

        ).containsExactly(
                StageStatus.WAITING,
                documentStage.getStageType(),
                StageType.DOCUMENT.getDescription(),
                documentStage.getSubmissionStatus()
        );
    }

    @Test
    void 면접_전형_생성_시_2번_저장된다_서류_PASS_면접() {
        // given
        var interviewNewStage = NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .build();

        var interviewStage = ApplicationStage.create(
                testApplication,
                StageType.INTERVIEW,
                StageType.INTERVIEW.getDescription(),
                null
        );
        given(applicationStageJpaRepository.save(any())).willReturn(interviewStage);

        // when
        var applicationStage = applicationStageManager.create(testApplication, interviewNewStage);

        // then
        assertThat(applicationStage).extracting(
                ApplicationStage::getStageStatus,
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getSubmissionStatus
        ).containsExactly(
                StageStatus.WAITING,
                interviewStage.getStageType(),
                StageType.INTERVIEW.getDescription(),
                null
        );

        verify(applicationStageJpaRepository, times(2)).save(any());
    }

    @Test
    void 기타_전형도_2번_저장된다_서류_PASS_기타() {
        // given
        var customStageName = "코딩테스트";
        var etcSchedule = new NewEtcSchedule(customStageName, LocalDateTime.now());
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newEtcSchedule(etcSchedule)
                .build();

        var etcStage = ApplicationStage.create(
                testApplication,
                StageType.ETC,
                customStageName,
                null
        );
        given(applicationStageJpaRepository.save(any())).willReturn(etcStage);

        // when
        var applicationStage = applicationStageManager.create(testApplication, etcNewStage);

        // then
        assertThat(applicationStage).extracting(
                ApplicationStage::getStageStatus,
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getSubmissionStatus

        ).containsExactly(
                StageStatus.WAITING,
                etcStage.getStageType(),
                customStageName,
                null
        );

        verify(applicationStageJpaRepository, times(2)).save(any());
    }

    @Test
    void 최종_전형은_FINAL_타입으로_저장된다() {
        // given
        var finalNewStage = NewStage.builder()
                .stageType(StageType.FINAL_PASS)
                .build();

        var etcStage = ApplicationStage.create(
                testApplication,
                StageType.FINAL_PASS,
                StageType.FINAL_PASS.getDescription(),
                null
        );
        given(applicationStageJpaRepository.save(any())).willReturn(etcStage);
        // when
        var applicationStage = applicationStageManager.create(testApplication, finalNewStage);

        // then
        assertThat(applicationStage).extracting(
                ApplicationStage::getStageStatus,
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getSubmissionStatus

        ).containsExactly(
                StageStatus.WAITING,
                finalNewStage.stageType(),
                finalNewStage.stageType().getDescription(),
                null
        );

        verify(applicationStageJpaRepository, times(2)).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = StageType.class, names = {"INTERVIEW", "FINAL_PASS", "FINAL_FAIL", "ETC"})
    void 서류_외_모든_전형은_서류_PASS_를_자동_생성한다(StageType stageType) {
        // given
        NewStage.NewStageBuilder builder = NewStage.builder()
                .stageType(stageType)
                .submissionStatus(SubmissionStatus.SUBMITTED);

        if (stageType == StageType.ETC) {
            builder.newEtcSchedule(new NewEtcSchedule("커스텀", LocalDateTime.now()));
        }

        var stage = builder.build();

        // when
        applicationStageManager.create(testApplication, stage);

        // then
        verify(applicationStageJpaRepository, times(2)).save(any());
    }

}