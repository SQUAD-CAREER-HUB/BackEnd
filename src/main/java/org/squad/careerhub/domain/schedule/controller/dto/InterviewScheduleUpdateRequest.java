package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ScheduleResult;

@Schema(description = "면접 일정 수정 요청 DTO (PATCH – 부분 수정)")
@Builder
public record InterviewScheduleUpdateRequest(

    @Schema(
        description = "면접 이름",
        example = "기술 면접"
    )
    @NotNull(message = "면접 이름은 필수 값입니다.")
    String scheduleName,

    @Schema(
        description = "면접 결과 상태 (WAITING 대기/ PASS 합격/ FAIL 불합격)",
        example = "PASS"
    )
    @NotNull(message = "면접 결과는 필수 값입니다.")
    ScheduleResult result,

    @Schema(
        description = "면접 일시 (ISO8601, LocalDateTime)",
        example = "2025-12-10T19:00:00"
    )
    @NotBlank(message = "면접일시는 필수 입력값입니다.")
    LocalDateTime startedAt,

    @Schema(description = "면접 장소", example = "서울 강남구 OO빌딩 3층 회의실")
    @NotBlank(message = "면접장소는 필수 입력값입니다.")
    String location
) {

}
