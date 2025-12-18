package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;

@Schema(description = "기타 전형 생성 요청 DTO")
public record EtcScheduleCreateRequest(
        @Schema(description = "전형 이름", example = "코딩 테스트")
        @NotBlank(message = "전형 이름은 필수 입력 항목입니다.")
        String stageName,

        @Schema(description = "전형 일정", example = "2025-03-25T14:30:00")
        LocalDateTime scheduledAt
) {

        public NewEtcSchedule toNewEtcSchedule() {
            return NewEtcSchedule.builder()
                    .stageName(stageName)
                    .scheduledAt(scheduledAt)
                    .build();
        }

}