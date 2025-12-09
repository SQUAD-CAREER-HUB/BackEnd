package org.squad.careerhub.domain.community.interviewreview.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.InterviewQuestionResponse;

@Schema(description = "면접 후기 상세 응답 DTO")
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

        @Schema(description = "작성일", example = "2024.06.15")
        String createdAt,

        @Schema(description = "작성자", example = "kimcoder")
        String author,

        @Schema(description = "좋아요 수", example = "10")
        Long likeCount,

        @Schema(description = "댓글 수", example = "5")
        Long commentCount,

        List<InterviewQuestionResponse> interviewQuestions
) {

    public static ReviewDetailResponse mock() {
        return new ReviewDetailResponse(
                1L,
                "TechCorp",
                "Software Engineer",
                "1차 기술 면접",
                "면접 과정이 매우 체계적이고 친절했습니다. 기술 질문이 깊이 있었습니다.",
                "2024.06.15",
                "kimcoder",
                10L,
                5L,
                List.of(
                        new InterviewQuestionResponse(1L, "이 회사에서 가장 중요하게 생각하는 가치관은 무엇인가요?"),
                        new InterviewQuestionResponse(2L, "최근에 진행한 프로젝트에 대해 설명해 주세요.")
                )
        );
    }

}