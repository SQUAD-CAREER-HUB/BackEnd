package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.enums.InterviewResult;
import org.squad.careerhub.domain.schedule.enums.InterviewStatus;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@Schema(description = "면접 일정 수정 요청 DTO (PATCH – 부분 수정)")
@Builder
public record InterviewScheduleUpdateRequest(

    @Schema(description = "면접 이름 (예: 1차 실무 면접)", example = "1차 실무 면접")
    String name,

    @Schema(
        description = "면접 유형",
        example = "TECH",
        allowableValues = {"TECH", "FIT", "EXECUTIVE", "TASK", "TEST", "ETC"}
    )
    @NotBlank(message = "면접 유형은 필수 입력값입니다.")
    InterviewType type,

    @Schema(
        description = "기타 면접 유형 상세 (type= ETC 일 때만 사용)",
        example = "인성 + 기술 면접"
    )
    String typeDetail,

    @Schema(
        description = "면접 상태",
        example = "SCHEDULED",
        allowableValues = {"SCHEDULED", "DONE", "CANCELED", "NONAPPEARANCE"}
    )
    InterviewStatus status,

    @Schema(
        description = "면접 결과 상태 (PENDING 대기/ PASS 합격/ FAIL 불합격)",
        example = "PASS"
    )
    InterviewResult result,

    @Schema(
        description = "면접 일시 (ISO8601, LocalDateTime)",
        example = "2025-12-10T19:00:00"
    )
    @NotBlank(message = "면접일시는 필수 입력값입니다.")
    LocalDateTime datetime,

    @Schema(description = "면접 장소", example = "서울 강남구 OO빌딩 3층 회의실")
    @NotBlank(message = "면접장소는 필수 입력값입니다.")
    String location,

    @Schema(
        description = "온라인 면접 링크",
        example = "https://zoom.us/j/123456789"
    )
    @NotBlank(message = "온라인 면접 링크는 필수 입력값입니다.")
    String onlineLink
) {
}
