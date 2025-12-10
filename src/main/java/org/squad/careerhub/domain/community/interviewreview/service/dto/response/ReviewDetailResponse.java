package org.squad.careerhub.domain.community.interviewreview.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.InterviewQuestionResponse;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;

@Schema(description = "면접 후기 상세 응답 DTO")
@Builder
public record ReviewDetailResponse(
        @Schema(description = "면접 후기 ID", example = "1")
        Long reviewId,

        @Schema(description = "회사 이름", example = "TechCorp")
        String company,

        @Schema(description = "포지션", example = "Software Engineer")
        String position,

        @Schema(description = "면접 종류", example = "1차 기술 면접")
        String interviewType,

        @Schema(description = "면접 후기 내용", example = "면접 과정이 매우 체계적이고 친절했습니다. 기술 질문이 깊이 있었습니다.")
        String content,

        @Schema(description = "작성일", example = "2024-06-15T14:30:00")
        LocalDateTime createdAt,

        @Schema(description = "작성자", example = "kimcoder")
        String author,

        @Schema(description = "작성자 여부", example = "true")
        boolean isAuthor,

        List<InterviewQuestionResponse> interviewQuestions
) {

    public static ReviewDetailResponse of(
            InterviewReview interviewReview,
            List<InterviewQuestionResponse> questionResponses,
            Long memberId
    ) {
        return ReviewDetailResponse.builder()
                .reviewId(interviewReview.getId())
                .company(interviewReview.getCompany())
                .position(interviewReview.getPosition())
                .interviewType(interviewReview.getInterviewType())
                .content(interviewReview.getContent())
                .createdAt(interviewReview.getCreatedAt())
                .author(interviewReview.getAuthor().getNickname())
                .isAuthor(interviewReview.isAuthor(memberId))
                .interviewQuestions(questionResponses)
                .build();
    }

}