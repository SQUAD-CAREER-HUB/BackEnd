package org.squad.careerhub.domain.archive.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Schema(description = "개인 면접 질문 페이지 응답 DTO (커서 기반 페이지네이션)")
@Builder
public record PersonalQuestionPageResponse(

    @Schema(description = "개인 면접 질문 목록")
    List<PersonalQuestionResponse> questions,

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    boolean hasNext,

    @Schema(description = "다음 커서 ID (마지막 질문 ID)", example = "25")
    Long nextCursorId
) {

    public static PersonalQuestionPageResponse mock() {
        PersonalQuestionResponse q1 = PersonalQuestionResponse.builder()
            .id(10L)
            .applicationId(1L)
            .interviewQuestionId(123L)
            .question("본인의 강점과 약점에 대해 말씀해 주세요.")
            .answer("제 강점은 문제를 끝까지 파고드는 집요함이고, 약점은 가끔 혼자 너무 깊게 몰입하는 점입니다.")
            .fromCommunity(true)
            .createdAt(LocalDateTime.of(2025, 11, 30, 19, 0))
            .updatedAt(LocalDateTime.of(2025, 11, 30, 19, 10))
            .build();

        PersonalQuestionResponse q2 = PersonalQuestionResponse.builder()
            .id(11L)
            .applicationId(1L)
            .interviewQuestionId(null)
            .question("최근에 가장 도전적이었던 프로젝트 경험을 설명해 주세요.")
            .answer("러닝 앱 프로젝트 개발 경험이 있습니다...")
            .fromCommunity(false)
            .createdAt(LocalDateTime.of(2025, 11, 30, 19, 5))
            .updatedAt(LocalDateTime.of(2025, 11, 30, 19, 20))
            .build();

        return PersonalQuestionPageResponse.builder()
            .questions(List.of(q1, q2))
            .hasNext(true)
            .nextCursorId(11L)
            .build();
    }


}
