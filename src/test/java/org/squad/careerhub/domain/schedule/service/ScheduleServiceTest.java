package org.squad.careerhub.domain.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.service.ApplicationReader;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.service.dto.ApplicationInfo;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleResponse;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class ScheduleServiceTest extends TestDoubleSupport {

    @Mock
    ScheduleManager scheduleManager;

    @Mock
    ApplicationReader applicationReader;

    @InjectMocks
    ScheduleService scheduleService;

    @Test
    void createInterviewFromCalendar_정상_소유한_지원서면_면접일정을_생성한다() {
        Long memberId = 1L;
        Long applicationId = 10L;

        Application app = Mockito.mock(Application.class);
        when(app.getId()).thenReturn(applicationId);
        when(applicationReader.findApplication(applicationId)).thenReturn(app);

        ApplicationStage stage = Mockito.mock(ApplicationStage.class);
        when(stage.getApplication()).thenReturn(app);

        Schedule saved = Mockito.mock(Schedule.class);
        when(saved.getId()).thenReturn(100L);
        when(saved.getApplicationStage()).thenReturn(stage);

        when(scheduleManager.createInterviewSchedule(eq(app), any(NewInterviewSchedule.class)))
            .thenReturn(saved);

        InterviewScheduleCreateRequest req = InterviewScheduleCreateRequest.builder()
            .applicationId(applicationId)
            .scheduleName("1차 기술면접")
            .startedAt(LocalDateTime.of(2025, 12, 10, 19, 0))
            .location("서울")
            .build();

        ScheduleResponse res = scheduleService.createInterviewFromCalendar(
            req.toApplicationInfo(),
            req.toNewInterviewSchedule(),
            memberId
        );

        assertThat(res.id()).isEqualTo(100L);

        verify(applicationReader).findApplication(applicationId);
        verify(app).validateOwnedBy(memberId);

        ArgumentCaptor<NewInterviewSchedule> captor = ArgumentCaptor.forClass(NewInterviewSchedule.class);
        verify(scheduleManager).createInterviewSchedule(eq(app), captor.capture());

        NewInterviewSchedule captured = captor.getValue();
        assertThat(captured.scheduleName()).isEqualTo("1차 기술면접");
        assertThat(captured.startedAt()).isEqualTo(LocalDateTime.of(2025, 12, 10, 19, 0));
        assertThat(captured.location()).isEqualTo("서울");
    }


    @Test
    void createInterviewFromCalendar_applicationId가_null이면_BAD_REQUEST() {
        // given
        ApplicationInfo info = new ApplicationInfo(null);
        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
            .scheduleName("1차 면접")
            .startedAt(now())
            .location("서울")
            .build();

        when(applicationReader.findApplication(null))
            .thenThrow(new CareerHubException(ErrorStatus.BAD_REQUEST));

        // when & then
        assertThatThrownBy(() -> scheduleService.createInterviewFromCalendar(info, cmd, 1L))
            .isInstanceOf(CareerHubException.class)
            .extracting("errorStatus")
            .isEqualTo(ErrorStatus.BAD_REQUEST);
    }

    @Test
    void createInterviewFromCalendar_지원서가_없으면_NOT_FOUND() {
        // given
        Long applicationId = 999L;

        when(applicationReader.findApplication(applicationId))
            .thenThrow(new CareerHubException(ErrorStatus.NOT_FOUND));

        ApplicationInfo info = new ApplicationInfo(applicationId);
        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
            .scheduleName("면접")
            .startedAt(now())
            .location("서울")
            .build();

        // when & then
        assertThatThrownBy(() -> scheduleService.createInterviewFromCalendar(info, cmd, 1L))
            .isInstanceOf(CareerHubException.class)
            .extracting("errorStatus")
            .isEqualTo(ErrorStatus.NOT_FOUND);
    }

    @Test
    void createInterviewFromCalendar_남의_지원서면_FORBIDDEN() {
        // given
        Long memberId = 1L;
        Long applicationId = 10L;

        Application app = Mockito.mock(Application.class);
        when(applicationReader.findApplication(applicationId)).thenReturn(app);

        Mockito.doThrow(new CareerHubException(ErrorStatus.FORBIDDEN_ERROR))
            .when(app).validateOwnedBy(memberId);

        ApplicationInfo info = new ApplicationInfo(applicationId);
        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
            .scheduleName("면접")
            .startedAt(now())
            .location("서울")
            .build();

        // when & then
        assertThatThrownBy(() -> scheduleService.createInterviewFromCalendar(info, cmd, memberId))
            .isInstanceOf(CareerHubException.class)
            .extracting("errorStatus")
            .isEqualTo(ErrorStatus.FORBIDDEN_ERROR);
    }

    @Test
    void createEtcFromCalendar_정상_소유한_지원서면_기타일정을_생성한다() {
        // given
        Long memberId = 1L;
        Long applicationId = 10L;

        Application app = Mockito.mock(Application.class);
        when(app.getId()).thenReturn(applicationId);

        when(applicationReader.findApplication(applicationId)).thenReturn(app);

        ApplicationStage stage = Mockito.mock(ApplicationStage.class);
        when(stage.getApplication()).thenReturn(app);
        when(stage.getStageType()).thenReturn(StageType.ETC);

        Schedule saved = Mockito.mock(Schedule.class);
        when(saved.getId()).thenReturn(200L);
        when(saved.getApplicationStage()).thenReturn(stage);

        when(scheduleManager.createEtcSchedule(eq(app), any(NewEtcSchedule.class)))
            .thenReturn(saved);

        EtcScheduleCreateRequest req = EtcScheduleCreateRequest.builder()
            .applicationId(applicationId)
            .scheduleName("과제 제출")
            .startedAt(LocalDateTime.of(2025, 12, 5, 23, 59))
            .endedAt(null)
            .build();

        ApplicationInfo info = req.toApplicationInfo();
        NewEtcSchedule cmd = req.toNewEtcSchedule();

        // when
        ScheduleResponse res = scheduleService.createEtcFromCalendar(info, cmd, memberId);

        // then
        assertThat(res.id()).isEqualTo(200L);

        verify(applicationReader).findApplication(applicationId);
        verify(app).validateOwnedBy(memberId);

        ArgumentCaptor<NewEtcSchedule> captor = ArgumentCaptor.forClass(NewEtcSchedule.class);
        verify(scheduleManager).createEtcSchedule(eq(app), captor.capture());

        NewEtcSchedule captured = captor.getValue();
        assertThat(captured.scheduleName()).isEqualTo("과제 제출");
        assertThat(captured.startedAt()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
        assertThat(captured.endedAt()).isNull();
    }

    private LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}