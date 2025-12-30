package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;

@Builder
@Schema(description = "캘린더에서 기타(ETC) 일정 생성 요청")
public record EtcScheduleCreateRequest(
        @Schema(description = "일정 제목", example = "과제 전형 제출")
        @NotBlank(message = "일정 제목은 필수 입력 항목입니다.")
        String scheduleName,

        @Schema(description = "일정 시작 일시")
        @NotNull
        LocalDateTime startedAt,

        @Schema(description = "일정 종료 일시")
        LocalDateTime endedAt,

        @Schema(description = "전형 결과(캘린더에서 생성 시 기본 WAITING)", example = "WAITING")
        ScheduleResult scheduleResult
) {

    public NewEtcSchedule toNewEtcSchedule() {
        return NewEtcSchedule.builder()
                .scheduleName(scheduleName)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .scheduleResult(scheduleResult == null ? ScheduleResult.WAITING : scheduleResult)
                .build();
    }

}