package org.squad.careerhub.domain.schedule.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleResponse;
import org.squad.careerhub.global.annotation.TestMember;

class ScheduleControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 면접_일정_생성_요청을_한다() throws Exception {
        // given
        var request = InterviewScheduleCreateRequest.builder()
            .applicationId(10L)
            .scheduleName("1차 기술면접")
            .startedAt(LocalDateTime.of(2025, 12, 10, 19, 0))
            .location("서울")
            .build();

        given(scheduleService.createInterviewFromCalendar(any(), any(), any()))
            .willReturn(ScheduleResponse.builder()
                .id(100L)
                .applicationId(10L)
                .company("Naver")
                .position("Backend Developer")
                .stageType(StageType.INTERVIEW)
                .stageName("면접")
                .scheduleName("1차 기술면접")
                .startedAt(LocalDateTime.of(2025, 12, 10, 19, 0))
                .endedAt(null)
                .location("서울")
                .scheduleResult(ScheduleResult.WAITING)
                .submissionStatus(null)
                .applicationStatus(ApplicationStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.of(2025, 12, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 12, 1, 10, 0))
                .build());


        // when & then
        assertThat(
            mvcTester.post()
                .uri("/v1/schedule/interview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .apply(print())
            .hasStatus(HttpStatus.CREATED);

        verify(scheduleService).createInterviewFromCalendar(any(), any(), any());
    }

    @TestMember
    @Test
    void 기타_일정_생성_요청을_한다() throws Exception {
        // given
        var request = EtcScheduleCreateRequest.builder()
            .applicationId(10L)
            .scheduleName("과제 제출")
            .startedAt(LocalDateTime.of(2025, 12, 5, 23, 59))
            .endedAt(null)
            .build();

        given(scheduleService.createEtcFromCalendar(any(), any(), any()))
            .willReturn(ScheduleResponse.builder()
                .id(200L)
                .applicationId(10L)
                .company("Naver")
                .position("Backend Developer")
                .stageType(StageType.ETC)
                .stageName("기타")
                .scheduleName("과제 제출")
                .startedAt(LocalDateTime.of(2025, 12, 5, 23, 59))
                .endedAt(null)
                .location(null)
                .scheduleResult(ScheduleResult.WAITING)
                .submissionStatus(null)
                .applicationStatus(ApplicationStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.of(2025, 12, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 12, 1, 10, 0))
                .build());

        // when & then
        assertThat(
            mvcTester.post()
                .uri("/v1/schedule/etc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .apply(print())
            .hasStatus(HttpStatus.CREATED);

        verify(scheduleService).createEtcFromCalendar(any(), any(), any());
    }
}
