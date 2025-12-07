package org.squad.careerhub.domain.application.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "지원 통계 응답 DTO")
@Builder
public record ApplicationStatisticsResponse(
        @Schema(description = "전체 지원 개수", example = "16")
        int totalApplications,

        @Schema(description = "면접 전형 중인 개수", example = "6")
        int interviewInProgress,

        @Schema(description = "서류 제출 필요한 개수", example = "2")
        int documentPending,

        @Schema(description = "최종 합격 개수", example = "1")
        int finalPassed
) {
    public static ApplicationStatisticsResponse mock() {
        return ApplicationStatisticsResponse.builder()
                .totalApplications(16)
                .interviewInProgress(6)
                .documentPending(2)
                .finalPassed(1)
                .build();
    }

}