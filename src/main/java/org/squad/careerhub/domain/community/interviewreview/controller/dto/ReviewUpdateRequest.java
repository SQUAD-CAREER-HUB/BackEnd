package org.squad.careerhub.domain.community.interviewreview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.UpdateReviewQuestion;
import org.squad.careerhub.domain.community.interviewreview.service.dto.UpdateInterviewReview;

@Schema(description = "면접 후기 수정 요청 DTO")
@Builder
public record ReviewUpdateRequest(
        @Schema(description = "회사 이름", example = "TechCorp")
        @NotBlank(message = "회사 이름은 필수 입력 항목 입니다.")
        String company,

        @Schema(description = "포지션", example = "Software Engineer")
        @NotBlank(message = "포지션은 필수 입력 항목 입니다.")
        String position,

        @Schema(description = "면접 종류", example = "1차 기술 면접")
        @NotBlank(message = "면접 종류는 필수 입력 항목 입니다.")
        String interviewType,

        @Schema(description = "면접 질문 리스트 (id가 null 이면 새로 생성, 기존 id가 요청에 없으면 삭제)")
        List<InterviewQuestionUpdateRequest> interviewQuestions,

        @Schema(description = "면접 후기 내용", example = "면접 과정이 매우 체계적이고 친절했습니다.")
        @NotBlank(message = "면접 후기 내용은 필수 입력 항목 입니다.")
        String content
) {

    @Schema(description = "면접 질문 수정 요청 DTO")
    public record InterviewQuestionUpdateRequest(
            @Schema(description = "질문 ID (null 이면 생성) ", example = "1")
            Long id,

            @Schema(description = "질문 내용", example = "자기소개를 해보세요.")
            @NotBlank(message = "질문 내용은 필수 입력 항목 입니다.")
            String question
    ) {

        public UpdateReviewQuestion toUpdateInterviewQuestion() {
            return UpdateReviewQuestion.builder()
                    .id(id)
                    .question(question)
                    .build();
        }
    }

    public List<UpdateReviewQuestion> toUpdateInterviewQuestions() {
        if (interviewQuestions == null) {
            return List.of();
        }

        return interviewQuestions.stream()
                .map(InterviewQuestionUpdateRequest::toUpdateInterviewQuestion)
                .toList();
    }

    public UpdateInterviewReview toUpdateInterviewReview() {
        return UpdateInterviewReview.builder()
                .company(company)
                .position(position)
                .interviewType(interviewType)
                .content(content)
                .build();
    }

}