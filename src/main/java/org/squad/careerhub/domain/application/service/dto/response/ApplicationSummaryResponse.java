package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;

@Schema(description = "지원서 요약 응답 DTO")
@Builder
public record ApplicationSummaryResponse(
        @Schema(description = "지원서 ID", example = "1")
        Long applicationId,

        @Schema(description = "회사 이름", example = "Naver")
        String company,

        @Schema(description = "포지션", example = "Backend Developer")
        String position,

        @Schema(description = "지원서 현재 전형 단계", example = "서류 전형")
        String currentStageType,

        @Schema(description = "지원서 현재 전형 상태", example = "PASS")
        String currentStageStatus,

        @Schema(description = "제출일")
        LocalDate submittedAt,

        @Schema(description = "마감일")
        LocalDate deadline,

        @Schema(description = "다음 면접 날짜")
        LocalDateTime nextInterviewDate
) {

    // QueryDSL용 enum을 받는 생성자 (새로 추가)
    public ApplicationSummaryResponse(
            Long applicationId,
            String company,
            String position,
            StageType currentStageTypeEnum,
            StageStatus currentStageStatusEnum,
            LocalDate submittedAt,
            LocalDate deadline,
            LocalDateTime nextInterviewDate
    ) {
        this(
                applicationId,
                company,
                position,
                currentStageTypeEnum.getDescription(),
                currentStageStatusEnum.name(),
                submittedAt,
                deadline,
                nextInterviewDate
        );
    }

    public ApplicationSummaryResponse withNextInterview(LocalDateTime nextInterview) {
        return ApplicationSummaryResponse.builder()
                .applicationId(applicationId)
                .company(company)
                .position(position)
                .currentStageType(currentStageType)
                .currentStageStatus(currentStageStatus)
                .submittedAt(submittedAt)
                .deadline(deadline)
                .nextInterviewDate(nextInterview)
                .build();
    }

}