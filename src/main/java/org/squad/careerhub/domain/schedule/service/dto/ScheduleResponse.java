package org.squad.careerhub.domain.schedule.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

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

    @Schema(description = "기타 일정 제목", example = "1차 기술 면접")
    String stageName,

    @Schema(
        description = "면접 유형 (stageType=INTERVIEW일 때만 사용)",
        example = "TECH",
        allowableValues = {"TECH", "FIT", "EXECUTIVE", "DESIGN", "TEST", "ETC"}
    )
    InterviewType interviewType,

    @Schema(description = "기타 면접 유형 상세 (interviewType=ETC일 때)", example = "인성 + 기술 면접")
    String interviewTypeDetail,

    @Schema(description = "일시 (ISO8601, LocalDateTime)", example = "2025-12-10T19:00:00")
    LocalDateTime datetime,

    @Schema(description = "장소", example = "서울 강남구 OO빌딩 3층 회의실")
    String location,

    @Schema(description = "링크(온라인/참고 링크)", example = "https://zoom.us/j/123456789")
    String link,

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
        var app = schedule.getApplication();

        return ScheduleResponse.builder()
            .id(schedule.getId())
            .applicationId(app.getId())
            .company(app.getCompany())
            .position(app.getPosition())
            .stageType(schedule.getStageType())
            .stageName(schedule.getStageName())
            .interviewType(schedule.getInterviewType())
            .interviewTypeDetail(schedule.getInterviewTypeDetail())
            .datetime(schedule.getDatetime())
            .location(schedule.getLocation())
            .link(schedule.getLink())
            .stageStatus(schedule.getStageStatus())
            .submissionStatus(schedule.getSubmissionStatus())
            .applicationStatus(schedule.getApplicationStatus())
            .createdAt(schedule.getCreatedAt())
            .updatedAt(schedule.getUpdatedAt())
            .build();
    }

    public static ScheduleResponse mock() {
        return ScheduleResponse.builder()
            .id(100L)
            .applicationId(1L)
            .company("구글 코리아")
            .position("백엔드 엔지니어")
            .stageType(StageType.INTERVIEW)
            .stageName(null)
            .interviewType(InterviewType.TECH)
            .interviewTypeDetail(null)
            .datetime(LocalDateTime.parse("2025-12-10T19:00:00"))
            .location("서울 강남구 OO빌딩 3층 회의실")
            .link("https://zoom.us/j/123456789")
            .stageStatus(StageStatus.WAITING)
            .submissionStatus(null)
            .applicationStatus(null)
            .createdAt(LocalDateTime.parse("2025-11-30T21:10:00"))
            .updatedAt(LocalDateTime.parse("2025-11-30T21:20:00"))
            .build();
    }

    public static ScheduleResponse mockEtc() {
        return ScheduleResponse.builder()
            .id(100L)
            .applicationId(1L)
            .company("구글 코리아")
            .position("백엔드 엔지니어")
            .stageType(StageType.ETC)
            .stageName("과제")
            .interviewType(null)
            .interviewTypeDetail(null)
            .datetime(LocalDateTime.parse("2025-12-10T19:00:00"))
            .location("서울 강남구 OO빌딩 3층 회의실")
            .link("https://zoom.us/j/123456789")
            .stageStatus(StageStatus.WAITING)
            .submissionStatus(null)
            .applicationStatus(null)
            .createdAt(LocalDateTime.parse("2025-11-30T21:10:00"))
            .updatedAt(LocalDateTime.parse("2025-11-30T21:20:00"))
            .build();
    }
}
