package org.squad.careerhub.domain.community.interviewquestion.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "면접 질문 수정 요청 DTO")
public record InterviewQuestionUpdateRequest(
        @Schema(description = "질문 ID (null 이면 생성) ", example = "1")
        Long id,

        @Schema(description = "질문 내용", example = "자기소개를 해보세요.")
        @NotBlank(message = "질문 내용은 필수 입력 항목 입니다.")
        String question
) {

}