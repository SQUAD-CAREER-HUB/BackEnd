package org.squad.careerhub.domain.archive.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.archive.service.dto.ApplicationQuestionArchiveResponse;
import org.squad.careerhub.global.annotation.TestMember;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

class QuestionArchiveControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void 지원서의_면접_질문_모음_조회를_요청한다() {
        // given
        PageResponse<ApplicationQuestionArchiveResponse> response = new PageResponse<>(
                List.of(ApplicationQuestionArchiveResponse.builder()
                        .questionArchiveId(1L)
                        .interviewType("기술면접")
                        .question("질문")
                        .build()),
                false,
                null
        );
        given(questionArchiveService.findArchivedQuestionsByApplication(1L, 1L, Cursor.of(1L, 10)))
                .willReturn(response);
        // when
        assertThat(mvcTester.get().uri("/v1/applications/{applicationId}/archived-questions", 1L)
                .param("lastCursorId", "1")
                .param("size", "10"))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.contents", v -> v.assertThat().isNotEmpty())
                .hasPathSatisfying("$.hasNext", v -> v.assertThat().isEqualTo(false))
                .hasPathSatisfying("$.nextCursorId", v -> v.assertThat().isNull());
    }

}