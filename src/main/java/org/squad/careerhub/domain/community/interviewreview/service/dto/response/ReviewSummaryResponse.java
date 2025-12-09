package org.squad.careerhub.domain.community.interviewreview.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;

@Schema(description = "면접 후기 미리보기 응답 DTO")
@Builder
public record ReviewSummaryResponse(
        @Schema(description = "면접 후기 ID", example = "1")
        Long reviewId,

        @Schema(description = "회사 이름", example = "TechCorp")
        String company,

        @Schema(description = "포지션", example = "Software Engineer")
        String position,

        @Schema(description = "면접 종류", example = "1차 기술 면접")
        String interviewType,

        @Schema(description = "면접 후기 간략 내용", example = "면접 과정이 매우 체계적이고 친절했습니다.")
        String shortContent,

        @Schema(description = "작성일", example = "2024.06.15")
        LocalDate createdAt,

        @Schema(description = "작성자", example = "kimcoder")
        String author
) {

    public static ReviewSummaryResponse from(InterviewReview interviewReview) {
        return ReviewSummaryResponse.builder()
                .reviewId(interviewReview.getId())
                .company(interviewReview.getCompany())
                .position(interviewReview.getPosition())
                .interviewType(interviewReview.getInterviewType())
                .shortContent(
                        interviewReview.getContent().length() > 100 ?
                                interviewReview.getContent().substring(0, 100) + "..." :
                                interviewReview.getContent()
                )
                .createdAt(interviewReview.getCreatedAt().toLocalDate())
                .author(interviewReview.getAuthor().getNickname())
                .build();
    }

}