package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.repository.InterviewType;
import org.squad.careerhub.domain.schedule.service.dto.InterviewScheduleUpdateParamRequest;

@Schema(description = "면접 일정 수정 요청 DTO (PATCH – 부분 수정)")
@Builder
public record InterviewScheduleUpdateRequest(

    @Schema(description = "면접 이름 (예: 1차 실무 면접)", example = "1차 실무 면접")
    String name,

    @Schema(
        description = "면접 유형",
        example = "TECH",
        allowableValues = {"TECH", "FIT", "EXEC", "TASK", "OTHER"}
    )
    String type,

    @Schema(
        description = "면접 일시 (ISO8601, LocalDateTime)",
        example = "2025-12-10T19:00:00"
    )
    LocalDateTime datetime,

    @Schema(description = "면접 장소", example = "서울 강남구 OO빌딩 3층 회의실")
    String location,

    @Schema(
        description = "온라인 면접 링크",
        example = "https://zoom.us/j/123456789"
    )
    String onlineLink
) {
    public InterviewScheduleUpdateParamRequest toParam() {
        return InterviewScheduleUpdateParamRequest.of(
            name,
            InterviewType.valueOf(type),
            datetime,
            location,
            onlineLink
        );
    }
}
