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
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleResponse;
import org.squad.careerhub.global.annotation.TestMember;

class ScheduleControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 면접_일정_생성_요청을_한다() throws Exception {
        // given
        var request = InterviewScheduleCreateRequest.builder()
            .applicationId(10L)
            .type(InterviewType.TECH)
            .typeDetail("1차 기술면접")
            .scheduledAt(LocalDateTime.of(2025, 12, 10, 19, 0))
            .location("서울")
            .link("https://zoom.us/...")
            .build();

        given(scheduleService.createInterviewFromCalendar(any(), any(), any()))
            .willReturn(ScheduleResponse.mock());

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
            .stageName("과제 제출")
            .scheduledAt(LocalDateTime.of(2025, 12, 5, 23, 59))
            .location("온라인")
            .link("https://...")
            .build();

        given(scheduleService.createEtcFromCalendar(any(), any(), any()))
            .willReturn(ScheduleResponse.mockEtc());

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
