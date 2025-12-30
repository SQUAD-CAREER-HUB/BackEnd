package org.squad.careerhub.domain.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

class ScheduleCreatorTest extends TestDoubleSupport {

    @Mock
    ScheduleJpaRepository scheduleJpaRepository;

    @Mock
    private ApplicationStageJpaRepository applicationStageJpaRepository;

    @InjectMocks
    ScheduleCreator scheduleCreator;

    private Application mockApplicationWithId(Long applicationId) {
        Application app = mock(Application.class);
        when(app.getId()).thenReturn(applicationId);
        return app;
    }

    private ApplicationStage mockStage() {
        return mock(ApplicationStage.class);
    }


    @Test
    void createInterviewSchedule_면접일정이면_SAVE한다() {
        // given
        Application app = mockApplicationWithId(10L);

        Member author = mock(Member.class);
        when(app.getAuthor()).thenReturn(author);

        ApplicationStage stage = mockStage();

        when(applicationStageJpaRepository.findByApplicationIdAndStageType(eq(10L),
                eq(StageType.INTERVIEW)))
                .thenReturn(Optional.of(stage));

        NewInterviewSchedule s1 = NewInterviewSchedule.builder()
                .scheduleName("1차")
                .startedAt(LocalDateTime.of(2025, 12, 10, 19, 0))
                .location("서울")
                .build();

        NewInterviewSchedule s2 = NewInterviewSchedule.builder()
                .scheduleName("임원")
                .startedAt(LocalDateTime.of(2025, 12, 12, 14, 0))
                .location("판교")
                .build();

        when(scheduleJpaRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        scheduleCreator.createInterviewSchedules(app, List.of(s1, s2));

        ArgumentCaptor<List<Schedule>> captor = ArgumentCaptor.forClass(List.class);
        verify(scheduleJpaRepository).saveAll(captor.capture());
        verify(scheduleJpaRepository, never()).save(any());

        List<Schedule> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved).extracting(Schedule::getScheduleName)
                .containsExactlyInAnyOrder("1차", "임원");
    }

    @Test
    void createInterviewSchedule_newInterviewSchedule이_null이면_NPE_and_save호출없다() {
        Application app = mock(Application.class);

        assertThatThrownBy(() -> scheduleCreator.createInterviewSchedule(app, null))
            .isInstanceOf(NullPointerException.class);

        verify(scheduleJpaRepository, never()).save(any());
        verify(scheduleJpaRepository, never()).saveAll(any());
    }

    @Test
    void createInterviewSchedules_application이_null이면_NPE_and_repo호출없다() {
        NewInterviewSchedule s1 = NewInterviewSchedule.builder()
                .scheduleName("1차 면접")
                .startedAt(now())
                .build();

        assertThatThrownBy(
                () -> scheduleCreator.createInterviewSchedules(null, List.of(s1)))
                .isInstanceOf(NullPointerException.class);

        verify(scheduleJpaRepository, never()).save(any());
        verify(scheduleJpaRepository, never()).saveAll(any());
    }

    @Test
    void createEtcSchedule_기타일정이면_SAVE_호출한다() {
        // given
        Application app = mock(Application.class);
        when(app.getId()).thenReturn(10L);

        Member author = mock(Member.class);
        when(app.getAuthor()).thenReturn(author);

        ApplicationStage stage = mock(ApplicationStage.class);
        when(stage.getStageType()).thenReturn(StageType.ETC);

        when(applicationStageJpaRepository.findByApplicationIdAndStageType(10L,
                StageType.ETC))
                .thenReturn(Optional.of(stage));

        NewEtcSchedule cmd = NewEtcSchedule.builder()
                .scheduleName("과제")
                .startedAt(LocalDateTime.of(2025, 12, 5, 23, 59))
                .endedAt(null)
                .build();

        when(scheduleJpaRepository.save(any(Schedule.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        Schedule saved = scheduleCreator.createEtcSchedule(app, cmd);

        // then
        verify(scheduleJpaRepository).save(any(Schedule.class));
        assertThat(saved.getApplicationStage().getStageType()).isEqualTo(StageType.ETC);
        assertThat(saved.getScheduleName()).isEqualTo("과제");
        assertThat(saved.getStartedAt()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
    }

    @Test
    void createEtcSchedule_application이_null이면_NPE_and_save호출없다() {
        NewEtcSchedule cmd = NewEtcSchedule.builder()
                .scheduleName("과제 제출")
                .startedAt(now())
                .endedAt(null)
                .build();

        assertThatThrownBy(() -> scheduleCreator.createEtcSchedule(null, cmd))
                .isInstanceOf(NullPointerException.class);

        verify(scheduleJpaRepository, never()).save(any());
    }
}

