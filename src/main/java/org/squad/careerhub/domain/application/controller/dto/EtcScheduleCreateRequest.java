package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.squad.careerhub.domain.application.service.dto.NewEtcSchedule;

@Schema(description = "기타 전형 생성 요청 DTO")
public record EtcScheduleCreateRequest(
        @Schema(description = "전형 이름", example = "코딩 테스트")
        String stageName,

        @Schema(description = "전형 일정", example = "2025-03-25T14:30:00")
        LocalDateTime scheduledAt
) {

        public NewEtcSchedule toNewEtcStage() {
            return NewEtcSchedule.builder()
                    .stageName(stageName)
                    .scheduledAt(scheduledAt)
                    .build();
        }

}