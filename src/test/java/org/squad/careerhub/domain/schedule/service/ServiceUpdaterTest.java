package org.squad.careerhub.domain.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;
import org.squad.careerhub.domain.schedule.service.dto.UpdateEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.UpdateInterviewSchedule;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

public class ServiceUpdaterTest extends TestDoubleSupport {

    private final ScheduleReader scheduleReader = mock(ScheduleReader.class);
    private final ScheduleJpaRepository scheduleJpaRepository = mock(ScheduleJpaRepository.class);

    private final ScheduleUpdater scheduleUpdater =
            new ScheduleUpdater(scheduleReader, scheduleJpaRepository);

    @Test
    void 면접_일정_수정_시_INTERVIEW_STAGEType으로_ACTIVE_스케줄을_조회하고_updateInterview를_호출한다() {
        // given
        Long appId = 10L;
        Long scheduleId = 100L;

        Application app = mock(Application.class);
        given(app.getId()).willReturn(appId);

        Schedule schedule = mock(Schedule.class);

        UpdateInterviewSchedule dto = new UpdateInterviewSchedule(
                "수정 면접",
                LocalDateTime.of(2025, 12, 11, 19, 0),
                "판교",
                ScheduleResult.WAITING
        );

        given(scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.INTERVIEW, EntityStatus.ACTIVE
                ))
                .willReturn(Optional.of(schedule));

        // when
        Schedule updated = scheduleUpdater.updateInterviewSchedule(app, scheduleId, dto);

        // then
        assertThat(updated).isSameAs(schedule);

        verify(scheduleJpaRepository)
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.INTERVIEW, EntityStatus.ACTIVE
                );

        verify(schedule).updateInterview(
                eq("수정 면접"),
                eq(LocalDateTime.of(2025, 12, 11, 19, 0)),
                eq("판교"),
                eq(ScheduleResult.WAITING)
        );
    }

    @Test
    void 면접_일정_수정_시_스케줄이_없으면_NOT_FOUND_예외를_던진다() {
        // given
        Long appId = 10L;
        Long scheduleId = 100L;

        Application app = mock(Application.class);
        given(app.getId()).willReturn(appId);

        UpdateInterviewSchedule dto = new UpdateInterviewSchedule(
                "수정 면접",
                LocalDateTime.of(2025, 12, 11, 19, 0),
                "판교",
                ScheduleResult.WAITING
        );

        given(scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.INTERVIEW, EntityStatus.ACTIVE
                ))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> scheduleUpdater.updateInterviewSchedule(app, scheduleId, dto))
                .isInstanceOf(CareerHubException.class)
                .satisfies(ex -> assertThat(((CareerHubException) ex).getErrorStatus())
                        .isEqualTo(ErrorStatus.NOT_FOUND));
    }

    @Test
    void 기타_일정_수정_시_ETC_STAGEType으로_ACTIVE_스케줄을_조회하고_updateEtc를_호출한다() {
        // given
        Long appId = 10L;
        Long scheduleId = 200L;

        Application app = mock(Application.class);
        given(app.getId()).willReturn(appId);

        Schedule schedule = mock(Schedule.class);

        UpdateEtcSchedule dto = new UpdateEtcSchedule(
                "수정 기타",
                LocalDateTime.of(2025, 12, 12, 10, 0),
                LocalDateTime.of(2025, 12, 12, 12, 0),
                ScheduleResult.WAITING
        );

        given(scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.ETC, EntityStatus.ACTIVE
                ))
                .willReturn(Optional.of(schedule));

        // when
        Schedule updated = scheduleUpdater.updateEtcSchedule(app, scheduleId, dto);

        // then
        assertThat(updated).isSameAs(schedule);

        verify(scheduleJpaRepository)
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.ETC, EntityStatus.ACTIVE
                );

        verify(schedule).updateEtc(
                eq("수정 기타"),
                eq(LocalDateTime.of(2025, 12, 12, 10, 0)),
                eq(LocalDateTime.of(2025, 12, 12, 12, 0)),
                eq(ScheduleResult.WAITING)
        );
    }

    @Test
    void 기타_일정_수정_시_스케줄이_없으면_NOT_FOUND_예외를_던진다() {
        // given
        Long appId = 10L;
        Long scheduleId = 200L;

        Application app = mock(Application.class);
        given(app.getId()).willReturn(appId);

        UpdateEtcSchedule dto = new UpdateEtcSchedule(
                "수정 기타",
                LocalDateTime.of(2025, 12, 12, 10, 0),
                LocalDateTime.of(2025, 12, 12, 12, 0),
                ScheduleResult.WAITING
        );

        given(scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.ETC, EntityStatus.ACTIVE
                ))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> scheduleUpdater.updateEtcSchedule(app, scheduleId, dto))
                .isInstanceOf(CareerHubException.class)
                .satisfies(ex -> assertThat(((CareerHubException) ex).getErrorStatus())
                        .isEqualTo(ErrorStatus.NOT_FOUND));
    }

    @Test
    void 일정_삭제_시_ETC_STAGEType으로_ACTIVE_스케줄을_조회하고_delete를_호출한다() {
        // given
        Long appId = 10L;
        Long scheduleId = 300L;

        Application app = mock(Application.class);
        given(app.getId()).willReturn(appId);

        Schedule schedule = mock(Schedule.class);

        given(scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.ETC, EntityStatus.ACTIVE
                ))
                .willReturn(Optional.of(schedule));

        // when
        scheduleUpdater.deleteSchedule(app, scheduleId);

        // then
        verify(scheduleJpaRepository)
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.ETC, EntityStatus.ACTIVE
                );
        verify(schedule).delete();
    }

    @Test
    void 일정_삭제_시_스케줄이_없으면_NOT_FOUND_예외를_던진다() {
        // given
        Long appId = 10L;
        Long scheduleId = 300L;

        Application app = mock(Application.class);
        given(app.getId()).willReturn(appId);

        given(scheduleJpaRepository
                .findByIdAndApplicationStage_Application_IdAndApplicationStage_StageTypeAndStatus(
                        scheduleId, appId, StageType.ETC, EntityStatus.ACTIVE
                ))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> scheduleUpdater.deleteSchedule(app, scheduleId))
                .isInstanceOf(CareerHubException.class)
                .satisfies(ex -> assertThat(((CareerHubException) ex).getErrorStatus())
                        .isEqualTo(ErrorStatus.NOT_FOUND));
    }
}
