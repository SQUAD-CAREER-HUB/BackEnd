package org.squad.careerhub.domain.schedule.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.entity.Schedule;

@Schema(description = "캘린더/전형 일정 통합 응답 DTO")
@Builder
public record ScheduleResponse(

    @Schema(description = "스케줄 ID", example = "100")
    Long id,

    @Schema(description = "지원서 ID", example = "1")
    Long applicationId,

    @Schema(description = "회사명", example = "구글 코리아")
    String company,

    @Schema(description = "포지션명", example = "백엔드 엔지니어")
    String position,

    @Schema(description = "전형 타입", example = "INTERVIEW",
        allowableValues = {"DOCUMENT", "ETC", "INTERVIEW", "APPLICATION_CLOSE"})
    StageType stageType,

    @Schema(description = "전형명(서류/면접/기타 등)", example = "1차 면접")
    String stageName,

    @Schema(description = "일정 제목", example = "과제 제출")
    String scheduleName,

    @Schema(description = "시작 일시 (ISO8601, LocalDateTime)", example = "2025-12-10T19:00:00")
    LocalDateTime startedAt,

    @Schema(description = "종료 일시 (ISO8601, LocalDateTime)", example = "2025-12-10T20:00:00")
    LocalDateTime endedAt,

    @Schema(description = "장소/링크", example = "서울 강남구 OO빌딩 3층 회의실 or zoom 링크")
    String location,

    @Schema(
        description = "전형 진행 상태 (대기/합격/불합)",
        example = "WAITING",
        allowableValues = {"WAITING", "PASS", "FAIL"}
    )
    StageStatus stageStatus,

    @Schema(
        description = "서류 제출 상태 (stageType=DOCUMENT일 때만 사용)",
        example = "SUBMITTED",
        allowableValues = {"SUBMITTED", "NOT_SUBMITTED"}
    )
    SubmissionStatus submissionStatus,

    @Schema(
        description = "지원서 최종 상태 (stageType=APPLICATION_CLOSE일 때만 사용)",
        example = "FINAL_PASS",
        allowableValues = {"FINAL_PASS", "FINAL_FAIL"}
    )
    ApplicationStatus applicationStatus,

    @Schema(description = "생성 시각", example = "2025-11-30T21:10:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 시각", example = "2025-11-30T21:20:00")
    LocalDateTime updatedAt
) {

    public static ScheduleResponse from(Schedule schedule) {
        ApplicationStage stage = schedule.getStage();
        Application app = stage.getApplication();

        return ScheduleResponse.builder()
            .id(schedule.getId())
            .applicationId(app.getId())
            .company(app.getCompany())
            .position(app.getPosition())

            .stageType(stage.getStageType())
            .stageName(stage.getStageName())

            .scheduleName(schedule.getScheduleName())

            .startedAt(schedule.getStartedAt())
            .endedAt(schedule.getEndedAt())
            .location(schedule.getLocation())

            .stageStatus(stage.getStageStatus())
            .submissionStatus(stage.getSubmissionStatus())

            // stageType=APPLICATION_CLOSE일 때는 Schedule이 아니라 Application이 들고 있는게 자연스러움
            .applicationStatus(app.getApplicationStatus())

            .createdAt(schedule.getCreatedAt())
            .updatedAt(schedule.getUpdatedAt())
            .build();
    }
}
