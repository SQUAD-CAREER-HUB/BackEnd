package org.squad.careerhub.domain.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

class ScheduleManagerTest extends TestDoubleSupport {

    @Mock
    ScheduleJpaRepository scheduleJpaRepository;

    @InjectMocks
    ScheduleManager scheduleManager;

    private Application mockOwnedApplication() {
        Member author = mock(Member.class);
        when(author.getId()).thenReturn(1L);

        Application app = mock(Application.class);
        when(app.getAuthor()).thenReturn(author);
        when(app.getApplicationStatus()).thenReturn(ApplicationStatus.IN_PROGRESS);

        return app;
    }


    @Test
    void createInterviewSchedule_면접일정이면_SAVE한다() {
        // given
        Application app = mockOwnedApplication();

        NewInterviewSchedule cmd = NewInterviewSchedule.builder()
            .stageType(StageType.INTERVIEW)
            .interviewType(InterviewType.TECH)
            .typeDetail("1차 기술면접")              // null 금지 가능성
            .scheduledAt(LocalDateTime.of(2025, 12, 10, 19, 0))
            .location("서울")                      // null 금지 가능성
            .link("https://zoom.us/...")           // null 금지 가능성
            .build();

        when(scheduleJpaRepository.save(any(Schedule.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        Schedule saved = scheduleManager.createInterviewSchedule(app, cmd);

        // then
        verify(scheduleJpaRepository).save(any(Schedule.class));
        assertThat(saved.getStageType()).isEqualTo(StageType.INTERVIEW);
        assertThat(saved.getInterviewType()).isEqualTo(InterviewType.TECH);
        assertThat(saved.getInterviewTypeDetail()).isEqualTo("1차 기술면접");
        assertThat(saved.getDatetime()).isEqualTo(LocalDateTime.of(2025, 12, 10, 19, 0));
        assertThat(saved.getLocation()).isEqualTo("서울");
        assertThat(saved.getLink()).isEqualTo("https://zoom.us/...");
    }

    @Test
    void createInterviewSchedules_여러개면_현재코드기준_saveN번_saveAll1번() {
        // given
        Application app = mockOwnedApplication();

        NewInterviewSchedule s1 = NewInterviewSchedule.builder()
            .stageType(StageType.INTERVIEW)
            .interviewType(InterviewType.TECH)
            .typeDetail("1차")
            .scheduledAt(LocalDateTime.of(2025, 12, 10, 19, 0))
            .location("서울")
            .link("https://a.com") // ✅ null 주지 말기
            .build();

        NewInterviewSchedule s2 = NewInterviewSchedule.builder()
            .stageType(StageType.INTERVIEW)
            .interviewType(InterviewType.EXECUTIVE)
            .typeDetail("임원")
            .scheduledAt(LocalDateTime.of(2025, 12, 12, 14, 0))
            .location("판교")
            .link("https://b.com")
            .build();

        when(scheduleJpaRepository.save(any(Schedule.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        when(scheduleJpaRepository.saveAll(anyList()))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        scheduleManager.createInterviewSchedules(app, List.of(s1, s2));

        // then (현재 구현 기준: save 2번 + saveAll 1번)
        verify(scheduleJpaRepository, times(2)).save(any(Schedule.class));
        verify(scheduleJpaRepository, times(1)).saveAll(anyList());
    }
    @Test
    void createInterviewSchedule_newInterviewSchedule이_null이면_NPE_and_save호출없다() {
        Application app = mock(Application.class);

        assertThatThrownBy(() -> scheduleManager.createInterviewSchedule(app, null))
            .isInstanceOf(NullPointerException.class);

        verify(scheduleJpaRepository, never()).save(any());
        verify(scheduleJpaRepository, never()).saveAll(any());
    }

    @Test
    void createInterviewSchedules_application이_null이면_NPE_and_repo호출없다() {
        NewInterviewSchedule s1 = NewInterviewSchedule.builder()
            .stageType(StageType.INTERVIEW)
            .interviewType(InterviewType.TECH)
            .scheduledAt(LocalDateTime.now())
            .build();

        assertThatThrownBy(() -> scheduleManager.createInterviewSchedules(null, List.of(s1)))
            .isInstanceOf(NullPointerException.class);

        verify(scheduleJpaRepository, never()).save(any());
        verify(scheduleJpaRepository, never()).saveAll(any());
    }

    @Test
    void createEtcSchedule_기타일정이면_SAVE_호출한다() {
        // given
        Application app = mockOwnedApplication();

        NewEtcSchedule cmd = NewEtcSchedule.builder()
            .stageType(StageType.ETC)
            .stageName("과제")                 // null 금지
            .scheduledAt(LocalDateTime.of(2025, 12, 5, 23, 59))
            .location("온라인")                    // null 금지 가능성
            .link("https://...")                   // null 금지 가능성
            .build();

        when(scheduleJpaRepository.save(any(Schedule.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        Schedule saved = scheduleManager.createEtcSchedule(app, cmd);

        // then
        verify(scheduleJpaRepository).save(any(Schedule.class));
        assertThat(saved.getStageType()).isEqualTo(StageType.ETC);
        assertThat(saved.getStageName()).isEqualTo("과제");
        assertThat(saved.getDatetime()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
        assertThat(saved.getLocation()).isEqualTo("온라인");
        assertThat(saved.getLink()).isEqualTo("https://...");
    }

    @Test
    void createEtcSchedule_application이_null이면_NPE_and_save호출없다() {
        NewEtcSchedule cmd = NewEtcSchedule.builder()
            .stageType(StageType.ETC)
            .stageName("과제 제출")
            .scheduledAt(LocalDateTime.now())
            .build();

        assertThatThrownBy(() -> scheduleManager.createEtcSchedule(null, cmd))
            .isInstanceOf(NullPointerException.class);

        verify(scheduleJpaRepository, never()).save(any());
    }
}

