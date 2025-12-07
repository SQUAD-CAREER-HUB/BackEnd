package org.squad.careerhub.domain.community.interviewreview.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Schema(description = "면접 후기 페이지 응답 DTO")
@Builder
public record ReviewPageResponse(
        List<ReviewSummaryResponse> reviews,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "마지막 면접 후기 ID", example = "123")
        Long lastReviewId,

        @Schema(description = "마지막 좋아요 수", example = "45")
        Long lastLikeCount
) {

    public static ReviewPageResponse mock() {
        ReviewSummaryResponse review1 = ReviewSummaryResponse.builder()
                .reviewId(1L)
                .company("네이버")
                .position("백엔드 개발자")
                .interviewType("1차 기술 면접")
                .shortContent("면접 과정이 매우 체계적이고 친절했습니다. 기술 질문이 깊이 있었습니다.")
                .createdAt(LocalDate.of(2024, 6, 15))
                .author("kimcoder")
                .likeCount(10L)
                .commentCount(5L)
                .build();

        ReviewSummaryResponse review2 = ReviewSummaryResponse.builder()
                .reviewId(2L)
                .company("카카오")
                .position("백엔드 개발자")
                .interviewType("2차 실무 면접")
                .shortContent("실무진과의 깊이 있는 대화가 인상적이었습니다.")
                .createdAt(LocalDate.of(2024, 6, 10))
                .author("devpark")
                .likeCount(8L)
                .commentCount(3L)
                .build();

        return ReviewPageResponse.builder()
                .reviews(List.of(review1, review2))
                .hasNext(true)
                .lastReviewId(2L)
                .lastLikeCount(8L)
                .build();
    }

}