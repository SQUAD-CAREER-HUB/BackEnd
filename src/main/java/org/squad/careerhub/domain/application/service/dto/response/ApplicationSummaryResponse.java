package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Schema(description = "지원서 요약 응답 DTO")
@Builder
public record ApplicationSummaryResponse(
        @Schema(description = "지원서 ID", example = "1")
        Long applicationId,

        @Schema(description = "회사 이름", example = "Naver")
        String company,

        @Schema(description = "포지션", example = "Backend Developer")
        String position,

        @Schema(description = "제출일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        LocalDate submittedAt,

        @Schema(description = "마감일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        LocalDate deadline,

        @Schema(description = "지원서 상태", example = "서류 제출 완료")
        String applicationStatus,

        @Schema(description = "다음 면접 날짜", example = "2025.04.10", type = "string", pattern = "yyyy.MM.dd")
        LocalDate nextInterviewDate
) {

}
