package org.squad.careerhub.domain.jobposting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.global.annotation.TestMember;

class JobPostingControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void url_파라미터로_채용공고_추출을_성공한다() throws Exception {
        // given
        String url = "https://www.rallit.com/positions/3974/멀티플랫폼-앱-개발자";
        JobPostingExtractResponse mockResponse = Mockito.mock(JobPostingExtractResponse.class);

        when(jobPostingService.extractJobPosting(anyString()))
            .thenReturn(mockResponse);

        // when & then
        assertThat(
            mvcTester.get()
                .uri("/v1/job-postings")
                .param("url", url)
        )
            .apply(print())
            .hasStatus(HttpStatus.OK);

        verify(jobPostingService).extractJobPosting(url);
    }

    @TestMember
    @Test
    void url_파라미터가_없으면_400을_반환한다() {
        assertThat(
            mvcTester.get()
                .uri("/v1/job-postings")
        )
            .apply(print())
            .hasStatus(HttpStatus.BAD_REQUEST);
    }
}