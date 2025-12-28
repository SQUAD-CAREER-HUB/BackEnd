package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.schedule.service.ScheduleManager;
import org.squad.careerhub.domain.schedule.service.dto.NewDocumentSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.application.service.dto.NewStage;

class ApplicationStageManagerUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationStageJpaRepository applicationStageJpaRepository;

    @Mock
    ScheduleManager scheduleManager;

    @InjectMocks
    private ApplicationStageManager applicationStageManager;

    private Application testApplication;

    @BeforeEach
    void setUp() {
        testApplication = mock(Application.class);
    }

    @Test
    void 서류_전형만_생성하면_1번만_저장되고_서류_일정_메서드를_호출한다() {
        // given
        var documentNewStage = NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .build();
        var documentStage = ApplicationStage.create(testApplication, StageType.DOCUMENT);
        given(applicationStageJpaRepository.save(any())).willReturn(documentStage);

        // when
        var result = applicationStageManager.createWithSchedule(testApplication, documentNewStage);

        // then
        assertThat(result.getStageType()).isEqualTo(StageType.DOCUMENT);
        verify(applicationStageJpaRepository, times(1)).save(any());
        verify(scheduleManager, times(1)).createDocumentSchedule(testApplication,
            new NewDocumentSchedule(documentNewStage.stageType(), testApplication.getDeadline(),
                documentNewStage.submissionStatus()));
    }

    @Test
    void 면접_전형_생성_시_서류_전형도_함꼐_저장되고_면접_일정_메서드를_호출한다() {
        // given
        var interviewNewStage = NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .build();

        var interviewStage = ApplicationStage.create(testApplication, StageType.INTERVIEW);
        given(applicationStageJpaRepository.save(any())).willReturn(interviewStage);

        // when
        var applicationStage = applicationStageManager.createWithSchedule(testApplication, interviewNewStage);

        // then
        assertThat(applicationStage.getStageType()).isEqualTo(StageType.INTERVIEW);
        verify(applicationStageJpaRepository, times(2)).save(any());
        verify(scheduleManager, times(1)).createInterviewSchedules(testApplication, interviewNewStage.newInterviewSchedules());
    }

    @Test
    void 기타_전형_생성_시_서류_전형도_함꼐_저장되고_기타_일정_메서드를_호출한다() {
        // given
        var customStageName = "코딩테스트";
        var etcSchedules = List.of(new NewEtcSchedule(StageType.ETC, customStageName, now(), now().plusDays(2)));
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newEtcSchedules(etcSchedules)
                .build();

        var etcStage = ApplicationStage.create(testApplication, StageType.ETC);
        given(applicationStageJpaRepository.save(any())).willReturn(etcStage);

        // when
        var applicationStage = applicationStageManager.createWithSchedule(testApplication, etcNewStage);

        // then
        assertThat(applicationStage.getStageType()).isEqualTo(StageType.ETC);
        verify(applicationStageJpaRepository, times(2)).save(any());
        verify(scheduleManager, times(1)).createEtcSchedule(testApplication, etcSchedules.getFirst());
    }

    private LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}