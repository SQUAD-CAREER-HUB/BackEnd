package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
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

        @Schema(description = "지원서 상태", example = "IN_PROGRESS ㅣ FINAL_PASS ㅣ FINAL_FAIL")
        String applicationStatus,

        @Schema(description = "현재 전형 단계 일정 결과 상태", example = "PASS ㅣ FAIL ㅣ WAITING")
        String currentScheduleResult,

        @Schema(description = "서류 전형 응답")
        DocsStage docsStage,

        @Schema(description = "기타 / 면접 전형 응답")
        ScheduleStage scheduleStage
) {

    public ApplicationSummaryResponse(
            Long applicationId,
            String company,
            String position,
            StageType currentStageType,
            ApplicationStatus applicationStatus,
            LocalDateTime deadline,
            ApplicationMethod applicationMethod,
            ScheduleResult currentScheduleResult,
            String stageName,
            String location,
            LocalDateTime startedAt
    ) {
        this(
                applicationId,
                company,
                position,
                currentStageType != null ? currentStageType.getDescription() : null,
                applicationStatus != null ? applicationStatus.name() : null,
                currentScheduleResult != null ? currentScheduleResult.name() : null,
                // 서류 전형일 때만 DocsStage 생성
                (currentStageType == StageType.DOCUMENT)
                        ? new DocsStage(deadline, applicationMethod.getDescription())
                        : null,
                // 면접/기타 전형일 때만 ScheduleStage 생성
                (currentStageType != StageType.DOCUMENT && startedAt != null)
                        ? new ScheduleStage(stageName, location, startedAt)
                        : null
        );
    }

}