package org.squad.careerhub.domain.schedule.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.EtcScheduleUpdateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleUpdateRequest;
import org.squad.careerhub.domain.schedule.enums.ResultCriteria;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleItem;
import org.squad.careerhub.domain.schedule.service.dto.UpdateEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.UpdateInterviewSchedule;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleListResponse;
import org.squad.careerhub.domain.schedule.service.dto.response.ScheduleResponse;
import org.squad.careerhub.global.annotation.TestMember;

class ScheduleControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 면접_일정_생성_요청을_한다() throws Exception {
        // given
        var request = InterviewScheduleCreateRequest.builder()
                .scheduleName("1차 기술면접")
                .startedAt(LocalDateTime.of(2025, 12, 10, 19, 0))
                .location("서울")
                .scheduleResult(ScheduleResult.WAITING)
                .build();

        given(scheduleService.createInterviewSchedule(
                        anyLong(),
                        any(NewInterviewSchedule.class),
                        anyLong()
                )
        )
                .willReturn(ScheduleResponse.builder()
                        .id(100L)
                        .applicationId(10L)
                        .company("Naver")
                        .position("Backend Developer")
                        .stageType(StageType.INTERVIEW)
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
                        .uri("/v1/applications/10/schedules/interview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        )
                .apply(print())
                .hasStatus(HttpStatus.CREATED);

        verify(scheduleService).createInterviewSchedule(any(), any(), any());
    }

    @TestMember
    @Test
    void 기타_일정_생성_요청을_한다() throws Exception {
        // given
        var request = EtcScheduleCreateRequest.builder()
                .scheduleName("과제 제출")
                .startedAt(LocalDateTime.of(2025, 12, 5, 23, 59))
                .endedAt(null)
                .scheduleResult(ScheduleResult.WAITING)
                .build();

        given(scheduleService.createEtcSchedule(any(), any(), any()))
                .willReturn(ScheduleResponse.builder()
                        .id(200L)
                        .applicationId(10L)
                        .company("Naver")
                        .position("Backend Developer")
                        .stageType(StageType.ETC)
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
                        .uri("/v1/applications/10/schedules/etc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        )
                .apply(print())
                .hasStatus(HttpStatus.CREATED);

        verify(scheduleService).createEtcSchedule(any(), any(), any());
    }

    @TestMember
    @Test
    void 일정조회_월범위_필터없음() {
        // given
        LocalDate from = LocalDate.of(2025, 12, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);

        ScheduleListResponse response = ScheduleListResponse.from(List.of(
                new ScheduleItem(
                        100L, 10L, "Naver", StageType.INTERVIEW,
                        "1차 기술면접", from.atStartOfDay(), null, "서울"
                )
        ));

        given(scheduleService.getSchedule(
                eq(from),
                eq(to),
                eq(""),
                eq(List.of(StageType.DOCUMENT, StageType.INTERVIEW, StageType.ETC)),
                eq(null),
                eq(null),
                any() // memberId
        )).willReturn(response);

        // when & then
        mvcTester.get()
                .uri("/v1/schedules?from=2025-12-01&to=2025-12-31&companyName=&stageTypes=DOCUMENT&stageTypes=INTERVIEW&stageTypes=ETC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK);

        verify(scheduleService).getSchedule(
                eq(from),
                eq(to),
                eq(""),
                eq(List.of(StageType.DOCUMENT, StageType.INTERVIEW, StageType.ETC)),
                eq(null),
                eq(null),
                any()
        );
    }

    @TestMember
    @Test
    void 일정조회_서류상태필터_SUBMITTED_NOT_SUBMITTED() {
        // given
        LocalDate from = LocalDate.of(2025, 12, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);

        List<SubmissionStatus> statuses = List.of(
                SubmissionStatus.SUBMITTED,
                SubmissionStatus.NOT_SUBMITTED
        );

        ScheduleListResponse response = ScheduleListResponse.from(List.of());

        given(scheduleService.getSchedule(
                eq(from),
                eq(to),
                eq(""),
                eq(List.of(StageType.DOCUMENT, StageType.INTERVIEW)),
                eq(statuses),
                eq(null),
                any()
        )).willReturn(response);

        // when & then
        mvcTester.get()
                .uri("/v1/schedules?from=2025-12-01&to=2025-12-31&companyName=&stageTypes=DOCUMENT&stageTypes=INTERVIEW&submissionStatuses=SUBMITTED&submissionStatuses=NOT_SUBMITTED")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK);

        verify(scheduleService).getSchedule(
                eq(from),
                eq(to),
                eq(""),
                eq(List.of(StageType.DOCUMENT, StageType.INTERVIEW)),
                eq(statuses),
                eq(null),
                any()
        );
    }

    @TestMember
    @Test
    void 일정조회_resultCriteria_포함() {
        // given
        LocalDate from = LocalDate.of(2025, 12, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);

        ScheduleListResponse response = ScheduleListResponse.from(List.of());

        given(scheduleService.getSchedule(
                eq(from),
                eq(to),
                eq(""),
                eq(List.of(StageType.INTERVIEW)),
                eq(null),
                eq(ResultCriteria.STAGE_PASS),
                any()
        )).willReturn(response);

        // when & then
        mvcTester.get()
                .uri("/v1/schedules?from=2025-12-01&to=2025-12-31&companyName=&stageTypes=INTERVIEW&resultCriteria=STAGE_PASS")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK);

        verify(scheduleService).getSchedule(
                eq(from),
                eq(to),
                eq(""),
                eq(List.of(StageType.INTERVIEW)),
                eq(null),
                eq(ResultCriteria.STAGE_PASS),
                any()
        );
    }

    @TestMember
    @Test
    void 일정_삭제_요청을_한다() {
        // given
        willDoNothing().given(scheduleService)
                .deleteSchedule(eq(10L), eq(300L), anyLong());

        // when & then
        assertThat(
                mvcTester.delete()
                        .uri("/v1/applications/10/schedules/300")
        )
                .apply(print())
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(scheduleService).deleteSchedule(eq(10L), eq(300L), anyLong());
    }

    @TestMember
    @Test
    void 기타_일정_수정_요청을_한다() throws Exception {
        // given
        var request = EtcScheduleUpdateRequest.builder()
                .scheduleName("수정된 기타 일정")
                .startedAt(LocalDateTime.of(2025, 12, 12, 10, 0))
                .endedAt(LocalDateTime.of(2025, 12, 12, 12, 0))
                .result(ScheduleResult.WAITING)
                .build();

        given(scheduleService.updateEtcSchedule(
                eq(10L),
                eq(200L),
                any(UpdateEtcSchedule.class),
                anyLong()
        )).willReturn(ScheduleResponse.builder()
                .id(200L)
                .applicationId(10L)
                .company("Naver")
                .position("Backend Developer")
                .stageType(StageType.ETC)
                .scheduleName("수정된 기타 일정")
                .startedAt(LocalDateTime.of(2025, 12, 12, 10, 0))
                .endedAt(LocalDateTime.of(2025, 12, 12, 12, 0))
                .location(null)
                .scheduleResult(ScheduleResult.WAITING)
                .submissionStatus(null)
                .applicationStatus(ApplicationStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.of(2025, 12, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 12, 2, 10, 0))
                .build());

        // when & then
        assertThat(
                mvcTester.put()
                        .uri("/v1/applications/10/schedules/etc/200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        )
                .apply(print())
                .hasStatus(HttpStatus.OK);

        verify(scheduleService).updateEtcSchedule(
                eq(10L),
                eq(200L),
                any(UpdateEtcSchedule.class),
                anyLong()
        );
    }

    @TestMember
    @Test
    void 면접_일정_수정_요청을_한다() throws Exception {
        // given
        var request = InterviewScheduleUpdateRequest.builder()
                .scheduleName("수정된 1차 기술면접")
                .startedAt(LocalDateTime.of(2025, 12, 11, 19, 0))
                .location("판교")
                .result(ScheduleResult.WAITING)
                .build();

        given(scheduleService.updateInterviewSchedule(
                eq(10L),
                eq(100L),
                any(UpdateInterviewSchedule.class),
                anyLong()
        )).willReturn(ScheduleResponse.builder()
                .id(100L)
                .applicationId(10L)
                .company("Naver")
                .position("Backend Developer")
                .stageType(StageType.INTERVIEW)
                .scheduleName("수정된 1차 기술면접")
                .startedAt(LocalDateTime.of(2025, 12, 11, 19, 0))
                .endedAt(null)
                .location("판교")
                .scheduleResult(ScheduleResult.WAITING)
                .submissionStatus(null)
                .applicationStatus(ApplicationStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.of(2025, 12, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 12, 2, 10, 0))
                .build());

        // when & then
        assertThat(
                mvcTester.put()
                        .uri("/v1/applications/10/schedules/interview/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        )
                .apply(print())
                .hasStatus(HttpStatus.OK);

        verify(scheduleService).updateInterviewSchedule(
                eq(10L),
                eq(100L),
                any(UpdateInterviewSchedule.class),
                anyLong()
        );
    }


}
