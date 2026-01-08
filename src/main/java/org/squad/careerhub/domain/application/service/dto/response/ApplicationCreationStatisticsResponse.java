package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "지원서 생성 통계 응답 DTO")
@Builder
public record ApplicationCreationStatisticsResponse(
        List<WeeklyStatistics> weeklyStatistics,
        List<MonthlyStatistics> monthlyStatistics
) {

    @Builder
    public record WeeklyStatistics(
            @Schema(description = "주간 기간", example = "01.02 - 01.08")
            String period,        // 예: "01.02 - 01.08"

            @Schema(description = "해당 주간에 생성된 지원서 수", example = "5")
            int count,

            @Schema(description = "현재 주간 여부", example = "true")
            boolean isCurrentWeek
    ) {

    }

    @Builder
    public record MonthlyStatistics(
            @Schema(description = "월간 기간", example = "2025.08")
            String period,        // 예: "2025.08"

            @Schema(description = "해당 월간에 생성된 지원서 수", example = "20")
            int count,

            @Schema(description = "현재 월간 여부", example = "true")
            boolean isCurrentMonth
    ) {

    }

}