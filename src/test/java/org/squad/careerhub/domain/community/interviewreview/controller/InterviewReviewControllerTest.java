package org.squad.careerhub.domain.community.interviewreview.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.squad.careerhub.ControllerTestSupport;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.InterviewQuestionResponse;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewCreateRequest;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewUpdateRequest;
import org.squad.careerhub.domain.community.interviewreview.controller.dto.ReviewUpdateRequest.InterviewQuestionUpdateRequest;
import org.squad.careerhub.domain.community.interviewreview.entity.SortType;
import org.squad.careerhub.domain.community.interviewreview.service.dto.response.ReviewDetailResponse;
import org.squad.careerhub.global.annotation.TestMember;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

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

    @TestMember
    @Test
    void 면접_후기_목록을_조회한다() {
        // when & then
        assertThat(mvcTester.get().uri("/v1/reviews")
                .param("sort", SortType.NEWEST.name())
                .param("size", "20"))
                .apply(print())
                .hasStatusOk();
    }

    @TestMember
    @Test
    void 검색어로_면접_후기_목록을_조회한다() {
        // when & then
        assertThat(mvcTester.get().uri("/v1/reviews")
                .param("query", "카카오")
                .param("sort", SortType.NEWEST.name())
                .param("size", "10"))
                .apply(print())
                .hasStatusOk();
    }

    @TestMember
    @Test
    void 커서_기반_페이징으로_다음_페이지를_조회한다() {
        // when & then
        assertThat(mvcTester.get().uri("/v1/reviews")
                .param("sort", SortType.NEWEST.name())
                .param("lastReviewId", "5")
                .param("size", "20"))
                .apply(print())
                .hasStatusOk();
    }

    @TestMember
    @Test
    void 면접_후기_상세_조회를_한다() {
        // given
        var reviewId = 1L;

        var expected = createReviewDetailResponse(reviewId);
        given(interviewReviewService.findReview(any(), any())).willReturn(expected);

        // when & then
        assertThat(mvcTester.get().uri("/v1/reviews/{reviewId}", reviewId))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.reviewId", v -> v.assertThat().isEqualTo((int) reviewId))
                .hasPathSatisfying("$.company", v -> v.assertThat().isEqualTo(expected.company()))
                .hasPathSatisfying("$.position", v -> v.assertThat().isEqualTo(expected.position()))
                .hasPathSatisfying("$.interviewType", v -> v.assertThat().isEqualTo(expected.interviewType()))
                .hasPathSatisfying("$.content", v -> v.assertThat().isEqualTo(expected.content()))
                .hasPathSatisfying("$.createdAt", v -> v.assertThat().isNotNull())
                .hasPathSatisfying("$.author", v -> v.assertThat().isEqualTo(expected.author()))
                .hasPathSatisfying("$.isAuthor", v -> v.assertThat().isEqualTo(expected.isAuthor()))
                .hasPathSatisfying("$.interviewQuestions.length()", v -> v.assertThat().isEqualTo(expected.interviewQuestions().size()));
    }

    @TestMember
    @Test
    void 존재하지_않는_면접_후기_조회시_404를_반환한다() {
        // given
        Long nonExistentReviewId = 999999L;

        given(interviewReviewService.findReview(any(), any()))
                .willThrow(new CareerHubException(ErrorStatus.NOT_FOUND_REVIEW));

        // when & then
        assertThat(mvcTester.get().uri("/v1/reviews/{reviewId}", nonExistentReviewId))
                .apply(print())
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @TestMember
    @Test
    void 면접_후기를_수정한다() throws JsonProcessingException {
        // given
        Long reviewId = 1L;

        var request = ReviewUpdateRequest.builder()
                .company("수정된 회사")
                .position("수정된 포지션")
                .interviewType("수정된 면접 유형")
                .content("수정된 내용입니다.")
                .interviewQuestions(List.of(
                        new InterviewQuestionUpdateRequest(1L, "수정된 질문1"),
                        new InterviewQuestionUpdateRequest(null, "새로운 질문")
                ))
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        willDoNothing().given(interviewReviewService).update(any(), any(), any(), any());

        // when & then
        assertThat(mvcTester.patch().uri("/v1/reviews/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @TestMember
    @Test
    void 작성자가_아닌_사용자가_수정시_403을_반환한다() throws JsonProcessingException {
        // given
        Long reviewId = 1L;

        var request = ReviewUpdateRequest.builder()
                .company("회사")
                .position("포지션")
                .interviewType("면접 유형")
                .content("내용입니다.")
                .interviewQuestions(List.of())
                .build();

        String requestJson = objectMapper.writeValueAsString(request);

        willThrow(new CareerHubException(ErrorStatus.FORBIDDEN_MODIFY))
                .given(interviewReviewService).update(any(), any(), any(), any());

        // when & then
        assertThat(mvcTester.patch().uri("/v1/reviews/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatus(HttpStatus.FORBIDDEN);
    }

    @TestMember
    @Test
    void 면접_후기를_삭제한다() {
        // given
        Long reviewId = 1L;

        willDoNothing().given(interviewReviewService).deleteReview(any(), any());

        // when & then
        assertThat(mvcTester.delete().uri("/v1/reviews/{reviewId}", reviewId))
                .apply(print())
                .hasStatus(HttpStatus.NO_CONTENT);
    }

    @TestMember
    @Test
    void 작성자가_아닌_사용자가_삭제시_403을_반환한다() {
        // given
        Long reviewId = 1L;

        willThrow(new CareerHubException(ErrorStatus.FORBIDDEN_DELETE))
                .given(interviewReviewService).deleteReview(any(), any());

        // when & then
        assertThat(mvcTester.delete().uri("/v1/reviews/{reviewId}", reviewId))
                .apply(print())
                .hasStatus(HttpStatus.FORBIDDEN);
    }

    public ReviewDetailResponse createReviewDetailResponse(Long reviewId) {
        return ReviewDetailResponse.builder()
                .reviewId(reviewId)
                .company("company")
                .position("position")
                .interviewType("interviewType")
                .content("content")
                .createdAt(now())
                .author("author")
                .isAuthor(true)
                .interviewQuestions(List.of(
                                new InterviewQuestionResponse(1L, "question1"),
                                new InterviewQuestionResponse(2L, "question2")
                        )
                )
                .build();
    }

    private LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}