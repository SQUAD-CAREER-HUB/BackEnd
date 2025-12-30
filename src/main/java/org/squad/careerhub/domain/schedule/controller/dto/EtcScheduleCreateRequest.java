package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;

@Builder
@Schema(description = "캘린더에서 기타(ETC) 일정 생성 요청")
public record EtcScheduleCreateRequest(

        @Schema(
                description = "일정 제목",
                example = "과제 전형 제출"
        )
        @NotNull
        String scheduleName,

        @Schema(
                description = "일정 시작 일시",
                example = "2025-12-05T23:59:00"
        )
        @NotNull
        LocalDateTime startedAt,

        @Schema(
                description = "일정 종료 일시",
                example = "2025-12-05T23:59:00"
        )
        LocalDateTime endedAt,

        @Schema(
                description = "장소",
                example = "온라인"
        )
        String location,

        @Schema(
                description = "링크",
                example = "https://..."
        )
        String link,

        @Schema(
                description = "일정 결과",
                example = "WAITING"
        )
        ScheduleResult scheduleResult
) {

    public NewEtcSchedule toNewEtcSchedule() {
        return NewEtcSchedule.builder()
                .scheduleName(scheduleName)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .result(scheduleResult)
                .build();
    }
}


