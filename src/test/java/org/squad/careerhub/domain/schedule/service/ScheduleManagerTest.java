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
import java.util.Optional;
import org.junit.jupiter.api.Test;
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

class ScheduleManagerTest extends TestDoubleSupport {

    @Mock
    ScheduleJpaRepository scheduleJpaRepository;

    @Mock
    private ApplicationStageJpaRepository applicationStageJpaRepository;

    @InjectMocks
    ScheduleManager scheduleManager;

    private Application mockApplicationWithId(long applicationId) {
        Application app = mock(Application.class);
        when(app.getId()).thenReturn(applicationId);
        return app;
    }

    private ApplicationStage mockStage(Application app, StageType stageType) {
        ApplicationStage stage = mock(ApplicationStage.class);
        when(stage.getStageType()).thenReturn(stageType);
        when(stage.getApplication()).thenReturn(app);
        return stage;
    }


    @Test
    void createInterviewSchedule_면접일정이면_SAVE한다() {
        // given
        Application app = mockApplicationWithId(10L);

        Member author = mock(Member.class);
        when(app.getAuthor()).thenReturn(author); // Schedule.createInterview 내부에서 author 필요할 수 있음

        ApplicationStage stage = mockStage(app, StageType.INTERVIEW);

        when(applicationStageJpaRepository.findByApplicationIdAndStageType(10L, StageType.INTERVIEW))
            .thenReturn(Optional.of(stage));

        NewInterviewSchedule s1 = NewInterviewSchedule.builder()
            .stageType(StageType.INTERVIEW)
            .scheduleName("1차")
            .startedAt(LocalDateTime.of(2025, 12, 10, 19, 0))
            .location("서울")
            .build();

        NewInterviewSchedule s2 = NewInterviewSchedule.builder()
            .stageType(StageType.INTERVIEW)
            .scheduleName("임원")
            .startedAt(LocalDateTime.of(2025, 12, 12, 14, 0))
            .location("판교")
            .build();

        when(scheduleJpaRepository.saveAll(anyList()))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        scheduleManager.createInterviewSchedules(app, List.of(s1, s2));

        // then
        verify(scheduleJpaRepository, times(1)).saveAll(anyList());
        verify(scheduleJpaRepository, never()).save(any(Schedule.class));
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
            .scheduleName("1차 면접")
            .startedAt(LocalDateTime.now())
            .build();

        assertThatThrownBy(() -> scheduleManager.createInterviewSchedules(null, List.of(s1)))
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
        when(stage.getApplication()).thenReturn(app);

        when(applicationStageJpaRepository.findByApplicationIdAndStageType(10L, StageType.ETC))
            .thenReturn(Optional.of(stage));

        NewEtcSchedule cmd = NewEtcSchedule.builder()
            .stageType(StageType.ETC)
            .scheduleName("과제")
            .startedAt(LocalDateTime.of(2025, 12, 5, 23, 59))
            .endedAt(null)
            .build();

        when(scheduleJpaRepository.save(any(Schedule.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        Schedule saved = scheduleManager.createEtcSchedule(app, cmd);

        // then
        verify(scheduleJpaRepository).save(any(Schedule.class));
        assertThat(saved.getStage().getStageType()).isEqualTo(StageType.ETC);
        assertThat(saved.getScheduleName()).isEqualTo("과제");
        assertThat(saved.getStartedAt()).isEqualTo(LocalDateTime.of(2025, 12, 5, 23, 59));
    }

    @Test
    void createEtcSchedule_application이_null이면_NPE_and_save호출없다() {
        NewEtcSchedule cmd = NewEtcSchedule.builder()
            .stageType(StageType.ETC)
            .scheduleName("과제 제출")
            .startedAt(LocalDateTime.now())
            .endedAt(null)
            .build();

        assertThatThrownBy(() -> scheduleManager.createEtcSchedule(null, cmd))
            .isInstanceOf(NullPointerException.class);

        verify(scheduleJpaRepository, never()).save(any());
    }
}

