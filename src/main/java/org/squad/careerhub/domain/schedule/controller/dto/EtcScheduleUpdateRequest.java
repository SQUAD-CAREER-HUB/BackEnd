package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.schedule.service.dto.UpdateEtcSchedule;

@Schema(description = "기타 전형 일정 수정 요청 DTO (PATCH – 부분 수정)")
@Builder
public record EtcScheduleUpdateRequest(
        @Schema(description = "일정 이름", example = "기술 면접")
        String scheduleName,

        @Schema(description = "일정 결과 상태 (WAITING 대기/ PASS 합격/ FAIL 불합격)", example = "PASS")
        ScheduleResult result,

        @Schema(description = "일정 시작 일시 (ISO8601, LocalDateTime)", example = "2025-12-10T19:00:00")
        @NotNull(message = "일정 시작 일시는 필수 입력값입니다.")
        LocalDateTime startedAt,

        @Schema(description = "일정 종료 일시 (ISO8601, LocalDateTime)", example = "2025-12-10T19:00:00")
        @NotNull(message = "일정 종료 일시는 필수 입력값입니다.")
        LocalDateTime endedAt
) {

    public UpdateEtcSchedule toUpdateEtcSchedule() {
        return UpdateEtcSchedule.builder()
                .scheduleName(scheduleName)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .scheduleResult(result == null ? ScheduleResult.WAITING : result)
                .build();
    }

}

