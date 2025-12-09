package org.squad.careerhub.domain.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.global.annotation.TestMember;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.application.controller.dto.ApplicationCreateRequest;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;

class ApplicationControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 지원서_생성_요청을_한다() throws JsonProcessingException {
        // given
        var request = createApplicationCreateRequest();
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(applicationService).createApplication(any(), any(), any());

        // when
        assertThat(mvcTester.post().uri("/v1/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(HttpStatus.CREATED);


        verify(applicationService).createApplication(any(), any(), any());
    }

    private ApplicationCreateRequest createApplicationCreateRequest() {
        return ApplicationCreateRequest.builder()
                .jobPostingUrl("https://www.naver.com/careers/12345")
                .company("Naver")
                .position("Backend Developer")
                .jobLocation("Seoul, Korea")
                .applicationStatus(ApplicationStatus.DOCUMENT_SUBMITTED)
                .deadline(LocalDate.of(2025, 3, 25))
                .submittedAt(LocalDate.of(2025, 3, 20))
                .applicationMethod(ApplicationMethod.HOMEPAGE)
                .build();
    }

}