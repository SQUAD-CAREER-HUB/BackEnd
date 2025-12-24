package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "기타, 면접 전형 일정 정보 DTO")
@Builder
public record ScheduleStage(
        @Schema(description = "전형 이름", example = "1차 면접 | 화상 면접 | 코딩 테스트 etc..")
        String stageName,

        @Schema(description = "장소 (기타 전형일 경우엔 Null)", example = "서울 강남구 테헤란로 123")
        String location,

        @Schema(description = "다음 일정 날짜", example = "2024-11-15T10:00:00")
        LocalDateTime nextScheduleAt
) {

}