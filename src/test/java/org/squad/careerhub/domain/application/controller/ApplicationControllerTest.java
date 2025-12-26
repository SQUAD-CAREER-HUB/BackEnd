package org.squad.careerhub.domain.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.application.controller.dto.ApplicationCreateRequest;
import org.squad.careerhub.domain.application.controller.dto.ApplicationInfoRequest;
import org.squad.careerhub.domain.application.controller.dto.JobPostingRequest;
import org.squad.careerhub.domain.application.controller.dto.StageRequest;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.global.annotation.TestMember;
import org.squad.careerhub.global.support.PageResponse;

class ApplicationControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 지원서_생성요청을_한다() throws JsonProcessingException {
        // given
        var request = createApplicationCreateRequest2();

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

        given(applicationService.createApplication(any(), any(), any(), any(), any()))
                .willReturn(1L);

        // when
        assertThat(mvcTester.perform(multipart("/v1/applications")
                .file(file)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        ))
                .apply(print())
                .hasStatus(HttpStatus.CREATED);

        verify(applicationService).createApplication(any(), any(), any(), any(), any());
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
                                LocalDate.now(),
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

    private ApplicationCreateRequest createApplicationCreateRequest2() {
        return ApplicationCreateRequest.builder()
                .jobPosting(JobPostingRequest.builder()
                        .company("Naver")
                        .jobLocation("Seoul, Korea")
                        .position("Backend Developer")
                        .jobPostingUrl("https://www.naver.com/careers/12345")
                        .build())
                .applicationInfo(
                        ApplicationInfoRequest.builder()
                                .applicationMethod(ApplicationMethod.HOMEPAGE)
                                .deadline(LocalDateTime.of(2025, 3, 25, 0, 0))
                                .build()
                )
                .stage(
                        StageRequest.builder()
                                .stageType(StageType.DOCUMENT)
                                .submissionStatus(SubmissionStatus.SUBMITTED)
                                .build()
                )
                .build();
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
        given(applicationService.getApplicationStatic(any())).willReturn(response);

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

}