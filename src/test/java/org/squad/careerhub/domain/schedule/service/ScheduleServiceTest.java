package org.squad.careerhub.domain.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.enums.ResultCriteria;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleItem;
import org.squad.careerhub.domain.schedule.service.dto.UpdateEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.UpdateInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleResponse;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class ScheduleServiceTest extends TestDoubleSupport {

    @Mock
    ScheduleCreator scheduleCreator;

    @Mock
    ApplicationReader applicationReader;

    @InjectMocks
    ScheduleService scheduleService;

    @Mock
    private ScheduleUpdater scheduleUpdater;

    @Mock
    private ScheduleReader scheduleReader;


    @Test
    void 정상_소유한_지원서면_면접일정을_생성한다() {
        Long memberId = 1L;
        Long applicationId = 10L;

        Application app = mock(Application.class);
        when(app.getId()).thenReturn(applicationId);
        when(applicationReader.findApplication(applicationId)).thenReturn(app);

        ApplicationStage stage = mock(ApplicationStage.class);
        when(stage.getApplication()).thenReturn(app);

        Schedule saved = mock(Schedule.class);
        when(saved.getId()).thenReturn(100L);
        when(saved.getApplicationStage()).thenReturn(stage);

        when(scheduleCreator.createInterviewSchedule(eq(app), any(NewInterviewSchedule.class)))
                .thenReturn(saved);

        InterviewScheduleCreateRequest req = InterviewScheduleCreateRequest.builder()
                .scheduleName("1차 기술면접")
                .startedAt(LocalDateTime.of(2025, 12, 10, 19, 0))
                .location("서울")
                .build();

        ScheduleResponse res = scheduleService.createInterviewSchedule(
                applicationId,
                req.toNewInterviewSchedule(),
                memberId
        );

        assertThat(res.id()).isEqualTo(100L);

        verify(applicationReader).findApplication(applicationId);
        verify(app).validateOwnedBy(memberId);

        ArgumentCaptor<NewInterviewSchedule> captor = ArgumentCaptor.forClass(
                NewInterviewSchedule.class);
        verify(scheduleCreator).createInterviewSchedule(eq(app), captor.capture());

        NewInterviewSchedule captured = captor.getValue();
        assertThat(captured.scheduleName()).isEqualTo("1차 기술면접");
        assertThat(captured.startedAt()).isEqualTo(LocalDateTime.of(2025, 12, 10, 19, 0));
        assertThat(captured.location()).isEqualTo("서울");
    }


    @Test
    void applicationId가_null이면_BAD_REQUEST() {
        // given
        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
                .scheduleName("1차 면접")
                .startedAt(now())
                .location("서울")
                .build();

        when(applicationReader.findApplication(null))
                .thenThrow(new CareerHubException(ErrorStatus.BAD_REQUEST));

        // when & then
        assertThatThrownBy(() -> scheduleService.createInterviewSchedule(null, cmd, 1L))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.BAD_REQUEST);
    }

    @Test
    void 지원서가_없으면_NOT_FOUND() {
        // given
        Long applicationId = 999L;

        when(applicationReader.findApplication(applicationId))
                .thenThrow(new CareerHubException(ErrorStatus.NOT_FOUND));

        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
                .scheduleName("면접")
                .startedAt(now())
                .location("서울")
                .build();

        // when & then
        assertThatThrownBy(
                () -> scheduleService.createInterviewSchedule(applicationId, cmd, 1L))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.NOT_FOUND);
    }

    @Test
    void 남의_지원서면_FORBIDDEN() {
        // given
        Long memberId = 1L;
        Long applicationId = 10L;

        Application app = mock(Application.class);
        when(applicationReader.findApplication(applicationId)).thenReturn(app);

        Mockito.doThrow(new CareerHubException(ErrorStatus.FORBIDDEN_ERROR))
                .when(app).validateOwnedBy(memberId);

        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
                .scheduleName("면접")
                .startedAt(now())
                .location("서울")
                .build();

        // when & then
        assertThatThrownBy(
                () -> scheduleService.createInterviewSchedule(applicationId, cmd, memberId))
                .isInstanceOf(CareerHubException.class)
                .extracting("errorStatus")
                .isEqualTo(ErrorStatus.FORBIDDEN_ERROR);
    }

    @Test
    void 정상_소유한_지원서면_기타일정을_생성한다() {
        // given
        Long memberId = 1L;
        Long applicationId = 10L;

        Application app = mock(Application.class);
        when(app.getId()).thenReturn(applicationId);

        when(applicationReader.findApplication(applicationId)).thenReturn(app);

        ApplicationStage stage = mock(ApplicationStage.class);
        when(stage.getApplication()).thenReturn(app);
        when(stage.getStageType()).thenReturn(StageType.ETC);

        Schedule saved = mock(Schedule.class);
        when(saved.getId()).thenReturn(200L);
        when(saved.getApplicationStage()).thenReturn(stage);

        when(scheduleCreator.createEtcSchedule(eq(app), any(NewEtcSchedule.class)))
                .thenReturn(saved);

        EtcScheduleCreateRequest req = EtcScheduleCreateRequest.builder()
                .scheduleName("과제 제출")
                .startedAt(LocalDateTime.of(2025, 12, 5, 23, 59))
                .endedAt(null)
                .build();

        NewEtcSchedule cmd = req.toNewEtcSchedule();

        // when
        ScheduleResponse res = scheduleService.createEtcSchedule(applicationId, cmd, memberId);

        // then
        assertThat(res.id()).isEqualTo(200L);

        verify(applicationReader).findApplication(applicationId);
        verify(app).validateOwnedBy(memberId);

        ArgumentCaptor<NewEtcSchedule> captor = ArgumentCaptor.forClass(NewEtcSchedule.class);
        verify(scheduleCreator).createEtcSchedule(eq(app), captor.capture());

        NewEtcSchedule captured = captor.getValue();
        assertThat(captured.scheduleName()).isEqualTo("과제 제출");
        assertThat(captured.startedAt()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
        assertThat(captured.endedAt()).isNull();
    }

    @Test
    void 면접_일정_수정_지원서_소유_검증_후_ScheduleUpdater를_호출한다() {
        // given
        Long applicationId = 10L;
        Long scheduleId = 100L;
        Long memberId = 1L;

        UpdateInterviewSchedule dto = new UpdateInterviewSchedule(
                "수정 면접",
                LocalDateTime.of(2025, 12, 11, 19, 0),
                "판교",
                org.squad.careerhub.domain.application.entity.ScheduleResult.WAITING
        );

        Application app = mock(Application.class);
        ApplicationStage stage = mock(ApplicationStage.class);
        Schedule updatedSchedule = mock(Schedule.class);

        given(applicationReader.findApplication(applicationId)).willReturn(app);
        willDoNothing().given(app).validateOwnedBy(memberId);
        given(scheduleUpdater.updateInterviewSchedule(app, scheduleId, dto)).willReturn(
                updatedSchedule);

        given(updatedSchedule.getApplicationStage()).willReturn(stage);
        given(stage.getApplication()).willReturn(app);
        given(app.getId()).willReturn(applicationId);

        // when
        ScheduleResponse response = scheduleService.updateInterviewSchedule(
                applicationId, scheduleId, dto, memberId
        );

        // then
        assertThat(response).isNotNull();
        verify(applicationReader).findApplication(applicationId);
        verify(app).validateOwnedBy(memberId);
        verify(scheduleUpdater).updateInterviewSchedule(app, scheduleId, dto);
    }

    @Test
    void 기타_일정_수정_지원서_소유_검증_후_ScheduleUpdater를_호출한다() {
        // given
        Long applicationId = 10L;
        Long scheduleId = 200L;
        Long memberId = 1L;

        UpdateEtcSchedule dto = new UpdateEtcSchedule(
                "수정 기타",
                LocalDateTime.of(2025, 12, 12, 10, 0),
                LocalDateTime.of(2025, 12, 12, 12, 0),
                org.squad.careerhub.domain.application.entity.ScheduleResult.WAITING
        );

        Application app = mock(Application.class);
        ApplicationStage stage = mock(ApplicationStage.class);
        Schedule updatedSchedule = mock(Schedule.class);

        given(applicationReader.findApplication(applicationId)).willReturn(app);
        willDoNothing().given(app).validateOwnedBy(memberId);
        given(scheduleUpdater.updateEtcSchedule(app, scheduleId, dto)).willReturn(updatedSchedule);
        given(updatedSchedule.getApplicationStage()).willReturn(stage);
        given(stage.getApplication()).willReturn(app);
        given(app.getId()).willReturn(applicationId);

        // when
        ScheduleResponse response = scheduleService.updateEtcSchedule(
                applicationId, scheduleId, dto, memberId
        );

        // then
        assertThat(response).isNotNull();
        verify(applicationReader).findApplication(applicationId);
        verify(app).validateOwnedBy(memberId);
        verify(scheduleUpdater).updateEtcSchedule(app, scheduleId, dto);
    }

    @Test
    void 일정_삭제_지원서_소유_검증_후_ScheduleUpdater_deleteSchedule을_호출한다() {
        // given
        Long applicationId = 10L;
        Long scheduleId = 300L;
        Long memberId = 1L;

        Application app = mock(Application.class);

        given(applicationReader.findApplication(applicationId)).willReturn(app);
        willDoNothing().given(app).validateOwnedBy(memberId);
        willDoNothing().given(scheduleUpdater).deleteSchedule(app, scheduleId);

        // when
        scheduleService.deleteSchedule(applicationId, scheduleId, memberId);

        // then
        verify(applicationReader).findApplication(applicationId);
        verify(app).validateOwnedBy(memberId);
        verify(scheduleUpdater).deleteSchedule(app, scheduleId);
    }

    @Test
    void 일정_조회_from_to를_LocalDateTime_범위로_변환해_QueryDslRepository에_전달한다() {
        // given
        LocalDate from = LocalDate.of(2025, 12, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);
        String companyName = "";
        List<StageType> stageTypes = List.of(StageType.DOCUMENT, StageType.INTERVIEW);
        List<SubmissionStatus> submissionStatuses = List.of(
                SubmissionStatus.SUBMITTED,
                SubmissionStatus.NOT_SUBMITTED
        );
        ResultCriteria resultCriteria = ResultCriteria.STAGE_PASS;
        Long memberId = 1L;

        ScheduleListResponse expected = ScheduleListResponse.from(List.of(
                new ScheduleItem(
                        100L,
                        10L,
                        "Naver",
                        StageType.INTERVIEW,
                        "1차 기술면접",
                        LocalDateTime.of(2025, 12, 10, 19, 0),
                        null,
                        "서울"
                )
        ));

        given(scheduleReader.getSchedule(
                from, to, companyName, stageTypes, submissionStatuses, resultCriteria, memberId
        )).willReturn(expected);

        // when
        ScheduleListResponse actual = scheduleService.getSchedule(
                from, to, companyName, stageTypes, submissionStatuses, resultCriteria, memberId
        );

        // then
        assertThat(actual).isSameAs(expected);
        verify(scheduleReader).getSchedule(
                from, to, companyName, stageTypes, submissionStatuses, resultCriteria, memberId
        );
    }
}