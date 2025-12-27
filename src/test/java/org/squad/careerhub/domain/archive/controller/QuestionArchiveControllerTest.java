package org.squad.careerhub.domain.archive.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;
import org.squad.careerhub.global.annotation.TestMember;

class QuestionArchiveControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 지원서의_면접_질문_모음_조회를_요청한다() {
        // given
        var responses = List.of(ApplicationQuestionArchiveResponse.builder()
                .questionArchiveId(1L)
                .interviewType("기술면접")
                .question("질문")
                .build()
        );
        given(questionArchiveService.findArchivedQuestionsByApplication(1L, 1L))
                .willReturn(responses);
        // when
        assertThat(mvcTester.get().uri("/v1/applications/{applicationId}/archived-questions", 1L))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$[0].questionArchiveId", v ->  v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$[0].interviewType", v -> v.assertThat().isEqualTo("기술면접"))
                .hasPathSatisfying("$[0].question", v -> v.assertThat().isEqualTo("질문"));
    }

}