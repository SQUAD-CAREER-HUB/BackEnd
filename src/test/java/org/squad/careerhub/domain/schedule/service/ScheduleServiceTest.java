package org.squad.careerhub.domain.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
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
    ApplicationJpaRepository applicationJpaRepository;

    @InjectMocks
    ScheduleService scheduleService;

    @Test
    void createInterviewFromCalendar_정상_소유한_지원서면_일정을_생성한다() {
        // given
        Long memberId = 1L;
        Long applicationId = 10L;

        Member author = Mockito.mock(Member.class);
        when(author.getId()).thenReturn(memberId);

        Application app = Mockito.mock(Application.class);
        when(app.getAuthor()).thenReturn(author);

        when(applicationJpaRepository.findById(applicationId)).thenReturn(Optional.of(app));

        Schedule saved = Mockito.mock(Schedule.class);
        when(saved.getId()).thenReturn(100L);

        when(scheduleManager.createInterviewSchedule(eq(app), any(NewInterviewSchedule.class)))
            .thenReturn(saved);

        when(saved.getApplication()).thenReturn(app);
        when(app.getId()).thenReturn(applicationId);

        // controller dto -> service dto 변환
        InterviewScheduleCreateRequest req = InterviewScheduleCreateRequest.builder()
            .applicationId(applicationId)
            .type(InterviewType.TECH)
            .typeDetail("1차 기술면접")
            .scheduledAt(LocalDateTime.of(2025, 12, 10, 19, 0))
            .location("서울")
            .link("https://zoom.us/...")
            .build();

        ApplicationInfo info = req.toApplicationInfo();
        NewInterviewSchedule cmd = req.toNewInterviewSchedule();

        // when
        ScheduleResponse res = scheduleService.createInterviewFromCalendar(info, cmd, memberId);

        // then
        assertThat(res.id()).isEqualTo(100L);
        verify(applicationJpaRepository).findById(applicationId);

        ArgumentCaptor<NewInterviewSchedule> captor = ArgumentCaptor.forClass(NewInterviewSchedule.class);
        verify(scheduleManager).createInterviewSchedule(eq(app), captor.capture());

        NewInterviewSchedule captured = captor.getValue();
        assertThat(captured.interviewType()).isEqualTo(InterviewType.TECH);
        assertThat(captured.typeDetail()).isEqualTo("1차 기술면접");
        assertThat(captured.scheduledAt()).isEqualTo(LocalDateTime.of(2025, 12, 10, 19, 0));
        assertThat(captured.location()).isEqualTo("서울");
        assertThat(captured.link()).isEqualTo("https://zoom.us/...");
    }

    @Test
    void createInterviewFromCalendar_applicationId가_null이면_BAD_REQUEST() {
        // given
        ApplicationInfo info = new ApplicationInfo(null);
        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
            .interviewType(InterviewType.TECH)
            .scheduledAt(LocalDateTime.now())
            .build();

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
        when(applicationJpaRepository.findById(applicationId)).thenReturn(Optional.empty());

        ApplicationInfo info = new ApplicationInfo(applicationId);
        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
            .interviewType(InterviewType.TECH)
            .scheduledAt(LocalDateTime.now())
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
        Long otherMemberId = 2L;
        Long applicationId = 10L;

        Member author = Mockito.mock(Member.class);
        when(author.getId()).thenReturn(otherMemberId);

        Application app = Mockito.mock(Application.class);
        when(app.getAuthor()).thenReturn(author);

        when(applicationJpaRepository.findById(applicationId)).thenReturn(Optional.of(app));

        ApplicationInfo info = new ApplicationInfo(applicationId);
        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
            .interviewType(InterviewType.TECH)
            .scheduledAt(LocalDateTime.now())
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

        Member author = Mockito.mock(Member.class);
        when(author.getId()).thenReturn(memberId);

        Application app = Mockito.mock(Application.class);
        when(app.getAuthor()).thenReturn(author);

        when(applicationJpaRepository.findById(applicationId)).thenReturn(Optional.of(app));

        Schedule saved = Mockito.mock(Schedule.class);
        when(saved.getId()).thenReturn(200L);

        when(scheduleManager.createEtcSchedule(eq(app), any(NewEtcSchedule.class)))
            .thenReturn(saved);

        when(saved.getApplication()).thenReturn(app);
        when(app.getId()).thenReturn(applicationId);

        EtcScheduleCreateRequest req = EtcScheduleCreateRequest.builder()
            .applicationId(applicationId)
            .stageName("과제 제출")
            .scheduledAt(LocalDateTime.of(2025, 12, 5, 23, 59))
            .location("온라인")
            .link("https://...")
            .build();

        ApplicationInfo info = req.toApplicationInfo();
        NewEtcSchedule cmd = req.toNewEtcSchedule();

        // when
        ScheduleResponse res = scheduleService.createEtcFromCalendar(info, cmd, memberId);

        // then
        assertThat(res.id()).isEqualTo(200L);
        verify(applicationJpaRepository).findById(applicationId);

        ArgumentCaptor<NewEtcSchedule> captor = ArgumentCaptor.forClass(NewEtcSchedule.class);
        verify(scheduleManager).createEtcSchedule(eq(app), captor.capture());

        NewEtcSchedule captured = captor.getValue();
        assertThat(captured.stageName()).isEqualTo("과제 제출");
        assertThat(captured.scheduledAt()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
        assertThat(captured.location()).isEqualTo("온라인");
        assertThat(captured.link()).isEqualTo("https://...");
    }
}