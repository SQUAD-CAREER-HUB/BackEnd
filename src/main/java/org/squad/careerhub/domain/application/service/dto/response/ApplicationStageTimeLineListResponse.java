package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.entity.Schedule;

@Schema(description = "지원서 전형 일정 타임라인 응답 DTO")
@Builder
public record ApplicationStageTimeLineListResponse(
        DocsStageTimeLine docsStageTimeLine,
        List<EtcStageTimeLine> etcStageTimeLine,
        List<InterviewStageTimeLine> interviewStageTimeLine
) {

    public static ApplicationStageTimeLineListResponse of(
            List<ApplicationStage> applicationStages,
            List<Schedule> schedules
    ) {
        Map<Long, List<Schedule>> schedulesByStage = schedules.stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getApplicationStage().getId()));

        Map<StageType, List<ApplicationStage>> stagesByType = applicationStages.stream()
                .collect(Collectors.groupingBy(ApplicationStage::getStageType));

        DocsStageTimeLine docsStageTimeLine = stagesByType.getOrDefault(StageType.DOCUMENT, List.of())
                .stream()
                .findFirst()
                .map(stage -> toDocsTimeLine(stage, schedulesByStage.getOrDefault(stage.getId(), List.of())))
                .orElse(null);

        List<EtcStageTimeLine> etcStageTimeLine = stagesByType.getOrDefault(StageType.ETC, List.of())
                .stream()
                .flatMap(stage -> toEtcTimeLines(stage, schedulesByStage.getOrDefault(stage.getId(), List.of())).stream())
                .toList();

        List<InterviewStageTimeLine> interviewStageTimeLine = stagesByType.getOrDefault(StageType.INTERVIEW, List.of())
                .stream()
                .flatMap(stage -> toInterviewTimeLines(stage, schedulesByStage.getOrDefault(stage.getId(), List.of())).stream())
                .toList();

        return ApplicationStageTimeLineListResponse.builder()
                .docsStageTimeLine(docsStageTimeLine)
                .etcStageTimeLine(etcStageTimeLine)
                .interviewStageTimeLine(interviewStageTimeLine)
                .build();
    }

    private static DocsStageTimeLine toDocsTimeLine(ApplicationStage stage, List<Schedule> schedules) {
        Schedule schedule = schedules.isEmpty() ? null : schedules.getFirst();

        return DocsStageTimeLine.builder()
                .stageId(stage.getId())
                .scheduleName(schedule != null ? schedule.getScheduleName() : null)
                .scheduleResult(schedule != null ? schedule.getScheduleResult() : null)
                .submissionStatus(schedule != null ? schedule.getSubmissionStatus() : null)
                .build();
    }

    private static List<EtcStageTimeLine> toEtcTimeLines(ApplicationStage stage, List<Schedule> schedules) {
        if (schedules.isEmpty()) {
            return List.of(EtcStageTimeLine.builder()
                    .stageId(stage.getId())
                    .build()
            );
        }

        return schedules.stream()
                .map(schedule -> EtcStageTimeLine.builder()
                        .stageId(stage.getId())
                        .scheduleName(schedule.getScheduleName())
                        .scheduleResult(schedule.getScheduleResult())
                        .startedAt(schedule.getStartedAt())
                        .endedAt(schedule.getEndedAt())
                        .build()
                ).toList();
    }

    private static List<InterviewStageTimeLine> toInterviewTimeLines(ApplicationStage stage, List<Schedule> schedules) {
        if (schedules.isEmpty()) {
            return List.of(InterviewStageTimeLine.builder()
                    .stageId(stage.getId())
                    .build()
            );
        }

        return schedules.stream()
                .map(schedule -> InterviewStageTimeLine.builder()
                        .stageId(stage.getId())
                        .scheduleName(schedule.getScheduleName())
                        .scheduleResult(schedule.getScheduleResult())
                        .location(schedule.getLocation())
                        .startedAt(schedule.getStartedAt())
                        .build()
                ).toList();
    }

    @Schema(description = "서류 전형 일정 타임라인 정보 DTO")
    @Builder
    public record DocsStageTimeLine(
            @Schema(description = "전형 ID", example = "1")
            Long stageId,

            @Schema(description = "전형 이름", example = "서류 전형")
            String scheduleName,

            @Schema(description = "전형 상태", example = " WAITING ㅣ PASS ㅣ FAILED")
            ScheduleResult scheduleResult,

            @Schema(description = "제출 상태", example = " NOT_SUBMITTED ㅣ SUBMITTED")
            SubmissionStatus submissionStatus
    ) {

    }

    @Schema(description = "기타 전형 일정 타임라인 정보 DTO")
    @Builder
    public record EtcStageTimeLine(
            @Schema(description = "전형 ID", example = "2")
            Long stageId,

            @Schema(description = "전형 이름", example = "코딩 테스트")
            String scheduleName,

            @Schema(description = "전형 상태", example = " WAITING ㅣ PASS ㅣ FAILED")
            ScheduleResult scheduleResult,

            @Schema(description = "시작일")
            LocalDateTime startedAt,

            @Schema(description = "종료일(NULL일 수 있음)")
            LocalDateTime endedAt
    ) {

    }

    @Schema(description = "면접 전형 일정 타임라인 정보 DTO")
    @Builder
    public record InterviewStageTimeLine(
            @Schema(description = "전형 ID", example = "3")
            Long stageId,

            @Schema(description = "전형 이름", example = "1차 면접")
            String scheduleName,

            @Schema(description = "전형 상태", example = " WAITING ㅣ PASS ㅣ FAILED")
            ScheduleResult scheduleResult,

            @Schema(description = "면접 장소", example = "서울 강남구 테헤란로 123")
            String location,

            @Schema(description = "면접 시작일")
            LocalDateTime startedAt
    ) {

    }

}