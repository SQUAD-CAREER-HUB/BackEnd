package org.squad.careerhub.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "회원 최근 활동 페이지 응답 DTO (커서 기반 페이지네이션)")
@Builder
public record MemberActivityPageResponse(

    @Schema(description = "활동 목록")
    List<MemberActivityResponse> activities,

    @Schema(
        description = "다음 페이지 존재 여부",
        example = "true"
    )
    boolean hasNext,

    @Schema(
        description = "다음 페이지 조회용 커서 ID",
        example = "101"
    )
    Long nextCursorId
) {

    public static MemberActivityPageResponse mock() {
        MemberActivityResponse a1 = MemberActivityResponse.mock();

        MemberActivityResponse a2 = MemberActivityResponse.builder()
            .activityId(100L)
            .type("INTERVIEW_CREATED")
            .targetType("INTERVIEW")
            .targetId(20L)
            .createdAt(a1.createdAt().minusHours(2))
            .build();

        return MemberActivityPageResponse.builder()
            .activities(List.of(a1, a2))
            .hasNext(true)
            .nextCursorId(100L)
            .build();
    }
}
