package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;

@Schema(description = "기타 전형 생성 요청 DTO")
public record EtcScheduleCreateRequest(
        @Schema(description = "전형 이름", example = "코딩 테스트")
        @NotBlank(message = "전형 이름은 필수 입력 항목입니다.")
        String scheduleName,

        @Schema(description = "전형 시작 일시", example = "2025-03-25T14:30:00")
        @NotNull(message = "전형 시작 일시는 필수 입력 항목입니다.")
        LocalDateTime startedAt,

        @Schema(description = "전형 종료 일시", example = "2025-03-27T14:30:00")
        LocalDateTime endedAt
) {

    public NewEtcSchedule toNewEtcSchedule() {
        return NewEtcSchedule.builder()
                .scheduleName(scheduleName)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }

}