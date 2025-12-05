package org.squad.careerhub.domain.community.interviewquestion.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "면접 질문 응답 DTO")
public record InterviewQuestionResponse(
        @Schema(description = "면접 질문 ID", example = "1")
        Long questionId,

        @Schema(description = "면접 질문 내용", example = "이 회사에서 가장 중요하게 생각하는 가치관은 무엇인가요?")
        String question

) {

}