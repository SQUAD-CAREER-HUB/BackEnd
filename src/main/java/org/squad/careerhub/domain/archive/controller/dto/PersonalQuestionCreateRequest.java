package org.squad.careerhub.domain.archive.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "개인 면접 질문 등록 요청 DTO")
@Builder
public record PersonalQuestionCreateRequest(

    @Schema(
        description = "커뮤니티 면접 질문 ID (커뮤니티에서 가져온 경우). 직접 작성한 질문이면 null",
        example = "123",
        nullable = true
    )
    Long interviewQuestionId,

    @Schema(
        description = "질문 내용 (직접 작성 시 필수)",
        example = "본인의 강점과 약점에 대해 말씀해 주세요.",
        nullable = true
    )
    String question,

    @Schema(
        description = "내 답변 내용",
        example = "제 강점은 문제를 끝까지 파고드는 집요함이고, 약점은 가끔 혼자 너무 깊게 몰입하는 점입니다..."
    )
    @NotBlank(message = "답변 내용은 필수 값입니다.")
    String answer
) {

    public PersonalQuestionCreateParamRequest toParam(Long memberId, Long applicationId) {
        return PersonalQuestionCreateParamRequest.of(
            memberId,
            applicationId,
            interviewQuestionId,
            question,
            answer
        );
    }
}

