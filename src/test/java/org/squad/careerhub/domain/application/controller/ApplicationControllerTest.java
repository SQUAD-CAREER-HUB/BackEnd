package org.squad.careerhub.domain.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.application.controller.dto.ApplicationCreateRequest;
import org.squad.careerhub.domain.application.controller.dto.ApplicationUpdateRequest;
import org.squad.careerhub.domain.application.controller.dto.DocsStageCreateRequest;
import org.squad.careerhub.domain.application.controller.dto.JobPostingRequest;
import org.squad.careerhub.domain.application.controller.dto.StageRequest;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationDetailPageResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationInfoResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStageTimeLineListResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStageTimeLineListResponse.DocsStageTimeLine;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.global.annotation.TestMember;
import org.squad.careerhub.global.support.PageResponse;

class ApplicationControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 지원서_생성요청을_한다() throws JsonProcessingException {
        // given
        var request = createApplicationCreateRequest();

        var file = new MockMultipartFile(
                "files",
                "profile.jpg",
                "image/jpeg",
                "profile image content".getBytes()
        );
        var requestPart = new MockMultipartFile(
                "request",
                "request.json",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        given(applicationService.createApplication(any(), any(), any(), any()))
                .willReturn(1L);

        // when
        assertThat(mvcTester.perform(multipart("/v1/applications")
                .file(file)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ))
                .apply(print())
                .hasStatus(HttpStatus.CREATED);

        verify(applicationService).createApplication(any(), any(), any(), any());
    }

    @TestMember
    @Test
    void 마감_되지_않은_서류전형의_지원서_조회_요청을_한다() {
        // given
        given(applicationService.findBeforeDeadlineApplications(any(), any()))
                .willReturn(PageResponse.<BeforeDeadlineApplicationResponse>builder()
                        .contents(List.of(new BeforeDeadlineApplicationResponse(
                                1L,
                                "c",
                                "p",
                                now(),
                                ApplicationMethod.EMAIL,
                                SubmissionStatus.SUBMITTED))
                        )
                        .hasNext(false)
                        .nextCursorId(null)
                        .build());
        // when & then
        assertThat(mvcTester.get().uri("/v1/applications/before-deadline")
                .param("size", "10"))
                .apply(print())
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.hasNext", v -> v.assertThat().isEqualTo(false))
                .hasPathSatisfying("$.nextCursorId", v -> v.assertThat().isNull())
                .hasPathSatisfying("$.contents", v -> v.assertThat().isNotEmpty());
    }

    @TestMember
    @Test
    void 지원서_통계를_조회한다() {
        // given
        var response = ApplicationStatisticsResponse.builder()
                .totalApplicationCount(100)
                .interviewStageCount(30)
                .etcStageCount(20)
                .finalPassedCount(25)
                .build();
        given(applicationService.getApplicationStatistics(any())).willReturn(response);

        // when & then
        assertThat(mvcTester.get().uri("/v1/applications/statistics"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.totalApplicationCount", v -> v.assertThat().isEqualTo(response.totalApplicationCount()))
                .hasPathSatisfying("$.interviewStageCount", v -> v.assertThat().isEqualTo(response.interviewStageCount()))
                .hasPathSatisfying("$.etcStageCount", v -> v.assertThat().isEqualTo(response.etcStageCount()))
                .hasPathSatisfying("$.finalPassedCount", v -> v.assertThat().isEqualTo(response.finalPassedCount()));
    }

    @TestMember
    @Test
    void 지원서_기본_정보와_첨부파일을_업데이트_한다() throws JsonProcessingException {
        // given
        var request = new ApplicationUpdateRequest(
                "https://www.careerhub.com/job/12345",
                "TechCorp",
                "Software Engineer",
                "New York, NY",
                "memo update"
        );
        var file = new MockMultipartFile(
                "files",
                "profile.jpg",
                "image/jpeg",
                "profile image content".getBytes()
        );
        var requestPart = new MockMultipartFile(
                "request",
                "request.json",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when & then
        assertThat(mvcTester.perform(multipart(HttpMethod.PATCH, "/v1/applications/{applicationId}", 1L)
                .file(file)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)))
                .apply(print())
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @TestMember
    @Test
    void 지원서_정보를_조회한다() {
        // given
        ApplicationDetailPageResponse mockResponse = createMockApplicationDetail();

        given(applicationService.findApplication(any(), any()))
                .willReturn(mockResponse);

        // when & then
        assertThat(mvcTester.get().uri("/v1/applications/{applicationId}", 1L))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.applicationInfo", v -> v.assertThat().isNotEmpty())
                .hasPathSatisfying("$.applicationStageTimeLine", v -> v.assertThat().isNotEmpty());
    }

    private ApplicationCreateRequest createApplicationCreateRequest() {
        return ApplicationCreateRequest.builder()
                .jobPosting(JobPostingRequest.builder()
                        .company("Naver")
                        .jobLocation("Seoul, Korea")
                        .position("Backend Developer")
                        .deadline(now().plusDays(2))
                        .jobPostingUrl("https://www.naver.com/careers/12345")
                        .build())
                .stage(
                        StageRequest.builder()
                                .stageType(StageType.DOCUMENT)
                                .docsStageCreateRequest(new DocsStageCreateRequest(
                                        SubmissionStatus.SUBMITTED,
                                        ApplicationMethod.EMAIL,
                                        ScheduleResult.WAITING)
                                )
                                .build()
                )
                .build();
    }

    private ApplicationDetailPageResponse createMockApplicationDetail() {
        ApplicationInfoResponse applicationInfo = ApplicationInfoResponse.builder()
                .applicationId(1L)
                .company("네이버")
                .position("백엔드 개발자")
                .jobLocation("서울")
                .jobPostingUrl("https://example.com")
                .currentStageType("면접 전형")
                .applicationStatus("IN_PROGRESS")
                .deadline(now().plusDays(30))
                .applicationMethod("온라인")
                .memo("메모")
                .attachedFiles(List.of())
                .build();

        ApplicationStageTimeLineListResponse timeline = ApplicationStageTimeLineListResponse.builder()
                .docsStageTimeLine(DocsStageTimeLine.builder()
                        .stageId(1L)
                        .scheduleName("서류 전형")
                        .scheduleResult(ScheduleResult.PASS)
                        .submissionStatus(SubmissionStatus.SUBMITTED)
                        .build())
                .etcStageTimeLine(List.of())
                .interviewStageTimeLine(List.of())
                .build();

        return ApplicationDetailPageResponse.builder()
                .applicationInfo(applicationInfo)
                .applicationStageTimeLine(timeline)
                .build();
    }
}