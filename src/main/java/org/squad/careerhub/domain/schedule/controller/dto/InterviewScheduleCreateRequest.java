package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.LocalDateTime;
import org.squad.careerhub.domain.schedule.repository.InterviewType;
import org.squad.careerhub.domain.schedule.service.dto.InterviewScheduleCreateParamRequest;

@Schema(description = "면접 일정 생성 요청 DTO")
@Builder
public record InterviewScheduleCreateRequest(

    @Schema(description = "지원 카드 ID", example = "1")
    @NotNull(message = "지원 카드 ID는 필수 값입니다.")
    Long applicationId,

    @Schema(description = "면접 이름 (예: 1차 실무 면접)", example = "1차 실무 면접")
    @NotBlank(message = "면접 이름은 필수 값입니다.")
    String name,

    @Schema(
        description = "면접 유형",
        example = "TECH",
        allowableValues = {"TECH", "FIT", "EXEC", "TASK", "TEST", "OTHER"},
        implementation = InterviewType.class
    )
    @NotNull(message = "면접 유형은 필수 값입니다.")
    InterviewType type,

    @Schema(
        description = "면접 일시 (ISO8601, LocalDateTime)",
        example = "2025-12-10T19:00:00"
    )
    @NotNull(message = "면접 일시는 필수 값입니다.")
    LocalDateTime datetime,

    @Schema(description = "면접 장소 (온라인일 경우 '온라인' 등으로 표시)", example = "서울 강남구 OO빌딩 3층 회의실")
    @NotBlank(message = "면접 장소는 필수 값입니다.")
    String location,

    @Schema(
        description = "온라인 면접 링크 (선택)",
        example = "https://zoom.us/j/123456789"
    )
    String onlineLink
) {
    public InterviewScheduleCreateParamRequest toParam(Long memberId) {
        return InterviewScheduleCreateParamRequest.of(
            memberId,
            applicationId,
            name,
            type,
            datetime,
            location,
            onlineLink
        );
    }
}
