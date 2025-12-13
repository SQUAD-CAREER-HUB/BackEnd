package org.squad.careerhub.domain.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
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
import org.squad.careerhub.global.annotation.TestMember;

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
                                .deadline(LocalDate.of(2025, 3, 25))
                                .submittedAt(LocalDate.of(2025, 3, 20))
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

}