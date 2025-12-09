package org.squad.careerhub.domain.archive.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "개인 면접 질문 단건 응답 DTO")
@Builder
public record PersonalQuestionResponse(
    @Schema(description = "개인 면접 질문 ID", example = "10")
    Long id,

    @Schema(description = "지원 카드 ID", example = "1")
    Long applicationId,

    @Schema(
        description = "커뮤니티 면접 질문 ID (커뮤니티에서 가져온 경우). 직접 작성한 질문이면 null",
        example = "123",
        nullable = true
    )
    Long interviewQuestionId,

    @Schema(description = "질문 내용", example = "본인의 강점과 약점에 대해 말씀해 주세요.")
    String question,

    @Schema(description = "내 답변 내용", example = "제 강점은 문제를 끝까지 파고드는 집요함이고...")
    String answer,

    @Schema(description = "커뮤니티에서 가져온 질문 여부", example = "true")
    boolean fromCommunity,

    @Schema(description = "생성 일시", example = "2025-11-30T19:00:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 일시", example = "2025-12-01T09:30:00")
    LocalDateTime updatedAt
) {

    public static PersonalQuestionResponse mock() {
        return PersonalQuestionResponse.builder()
            .id(10L)
            .applicationId(1L)
            .interviewQuestionId(123L)
            .question("본인의 강점과 약점에 대해 말씀해 주세요.")
            .answer("제 강점은 문제를 끝까지 파고드는 집요함이고, 약점은 가끔 혼자 깊게 몰입하는 점입니다...")
            .fromCommunity(true)
            .createdAt(LocalDateTime.of(2025, 11, 30, 19, 0))
            .updatedAt(LocalDateTime.of(2025, 11, 30, 19, 10))
            .build();
    }

    public static PersonalQuestionResponse updateMock() {
        return PersonalQuestionResponse.builder()
            .id(10L)
            .applicationId(1L)
            .interviewQuestionId(123L)
            .question("이 회사의 백엔드 인프라 구조에 대해 설명해 주세요.")
            .answer("현재는 MSA 기반으로 전환 중이며, 트래픽이 높은 도메인은 별도 스케일아웃을 적용하고 있습니다.")
            .fromCommunity(true)
            .createdAt(LocalDateTime.of(2025, 11, 30, 19, 0))
            .updatedAt(LocalDateTime.of(2025, 11, 30, 19, 10))
            .build();
    }
}