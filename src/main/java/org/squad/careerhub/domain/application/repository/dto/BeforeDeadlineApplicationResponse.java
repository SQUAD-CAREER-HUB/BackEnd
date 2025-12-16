package org.squad.careerhub.domain.application.repository.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;

@Schema(description = "마감 전 지원서 응답 DTO")
@Builder
public record BeforeDeadlineApplicationResponse(
        @Schema(description = "지원서 ID", example = "1")
        Long applicationId,

        @Schema(description = "회사 이름", example = "Naver")
        String company,

        @Schema(description = "포지션", example = "Backend Developer")
        String position,

        @Schema(description = "제출일")
        LocalDate submittedAt,

        @Schema(description = "마감일")
        LocalDate deadline,

        @Schema(description = "제출 상태", example = "제출 완료")
        SubmissionStatus submissionStatus
) {

}