package org.squad.careerhub.domain.archive.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.squad.careerhub.domain.archive.service.dto.PersonalQuestionUpdateParamRequest;

@Schema(description = "개인 면접 질문 수정 요청 DTO (Controller 레이어)")
@Builder
public record PersonalQuestionUpdateRequest(

    @Schema(
        description = "수정할 질문 내용 (null이면 질문 내용은 그대로 유지)",
        example = "이 회사의 백엔드 인프라 구조에 대해 설명해 주세요.",
        maxLength = 500
    )
    @Size(max = 500, message = "질문은 최대 500자까지 작성할 수 있습니다.")
    String question,

    @Schema(
        description = "수정할 답변 내용 (null이면 답변 내용은 그대로 유지)",
        example = "현재는 MSA 기반으로 전환 중이며, 트래픽이 높은 도메인은 별도 스케일아웃을 적용하고 있습니다."
    )
    String answer
) {

    public PersonalQuestionUpdateParamRequest toParam(
        Long memberId,
        Long applicationId,
        Long questionId
    ) {
        return PersonalQuestionUpdateParamRequest.of(
            memberId,
            applicationId,
            questionId,
            question,
            answer
        );
    }
}
