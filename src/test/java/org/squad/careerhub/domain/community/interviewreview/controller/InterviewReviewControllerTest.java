package org.squad.careerhub.domain.community.interviewreview.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewCreateRequest;
import org.squad.careerhub.global.annotation.TestMember;

class InterviewReviewControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 면접_후기_생성_요청을_한다() throws JsonProcessingException {
        // given
        var request = new ReviewCreateRequest(
                "삼성전자",
                "SW 개발자",
                "코딩 면접",
                List.of(),
                "content 내용입니다."
        );
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.post().uri("/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(HttpStatus.CREATED);
    }

}