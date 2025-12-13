package org.squad.careerhub.domain.jobposting;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.jobposting.controller.JobPostingController;
import org.squad.careerhub.domain.jobposting.service.JobPostingService;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.squad.careerhub.global.annotation.TestMember;

@WebMvcTest(JobPostingController.class)
class JobPostingControllerTest extends ControllerTestSupport {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JobPostingService jobPostingService;

    @TestMember
    @Test
    void url_파라미터로_채용공고_추출을_성공한다() throws Exception {
        // given
        String url = "https://www.rallit.com/positions/3974/멀티플랫폼-앱-개발자";
        JobPostingExtractResponse mockResponse = Mockito.mock(JobPostingExtractResponse.class);

        when(jobPostingService.extractJobPosting(anyString()))
            .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/v1/job-postings")
                    .param("url", url)
            ).andExpect(status().isOk());
    }

    @TestMember
    @Test
    void url_파라미터가_없으면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/v1/job-postings"))
            .andExpect(status().isBadRequest());
    }
}