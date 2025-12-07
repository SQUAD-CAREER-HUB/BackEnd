package org.squad.careerhub.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.member.service.dto.MemberActivityResultResponse;

@Schema(description = "회원 최근 활동 단건 응답 DTO")
@Builder
public record MemberActivityResponse(

    @Schema(description = "활동 ID (커서로도 사용)", example = "101")
    Long activityId,

    @Schema(
        description = "활동 유형",
        example = "APPLICATION_CREATED",
        allowableValues = {
            "APPLICATION_CREATED",
            "APPLICATION_UPDATED",
            "INTERVIEW_CREATED",
            "REVIEW_CREATED"
        }
    )
    String type,

    @Schema(
        description = "연결 리소스 유형",
        example = "APPLICATION"
    )
    String targetType,

    @Schema(
        description = "연결 리소스 ID",
        example = "10"
    )
    Long targetId,

    @Schema(
        description = "활동 일시",
        example = "2025-11-30T21:00:00"
    )
    LocalDateTime createdAt
) {

    public static MemberActivityResponse fromResult(MemberActivityResultResponse result) {
        return MemberActivityResponse.builder()
            .activityId(result.activityId())
            .type(result.type())
            .targetType(result.targetType())
            .targetId(result.targetId())
            .createdAt(result.createdAt())
            .build();
    }

    public static MemberActivityResponse mock() {
        return MemberActivityResponse.builder()
            .activityId(101L)
            .type("APPLICATION_CREATED")
            .targetType("APPLICATION")
            .targetId(10L)
            .createdAt(LocalDateTime.of(2025, 11, 30, 21, 0, 0))
            .build();
    }
}
