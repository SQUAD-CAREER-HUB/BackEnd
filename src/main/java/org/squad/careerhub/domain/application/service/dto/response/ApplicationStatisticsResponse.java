package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "지원 통계 응답 DTO")
@Builder
public record ApplicationStatisticsResponse(
        @Schema(description = "전체 지원 개수", example = "16")
        int totalApplicationCount,

        @Schema(description = "서류 전형 지원서 개수", example = "7")
        int docStageCount,

        @Schema(description = "면접 전형 지원서 개수", example = "6")
        int interviewStageCount,

        @Schema(description = "기타 전형 지원서 개수", example = "2")
        int etcStageCount,

        @Schema(description = "최종 합격 지원서 개수", example = "1")
        int finalPassedCount,

        @Schema(description = "최종 불합격 지원서 개수", example = "0")
        int finalFailedCount
) {

    public static ApplicationStatisticsResponse of(
            int totalApplicationCount,
            int docStageCount,
            int interviewStageCount,
            int etcStageCount,
            int finalPassedCount,
            int finalFailedCount
    ) {
        return ApplicationStatisticsResponse.builder()
                .totalApplicationCount(totalApplicationCount)
                .docStageCount(docStageCount)
                .interviewStageCount(interviewStageCount)
                .etcStageCount(etcStageCount)
                .finalPassedCount(finalPassedCount)
                .finalFailedCount(finalFailedCount)
                .build();
    }

}