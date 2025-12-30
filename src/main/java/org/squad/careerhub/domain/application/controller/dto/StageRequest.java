package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

@Schema(description = "전형 단계 요청 DTO")
@Builder
public record StageRequest(
        @Schema(description = "전형 단계", example = "DOCUMENT")
        @NotNull(message = "전형 단계는 필수 입력 항목입니다.")
        StageType stageType,

        @Schema(description = "서류 전형 정보 (서류 전형일 때만 입력)")
        @Valid
        DocsStageCreateRequest docsStageCreateRequest,

        @Schema(description = "기타 전형 일정 (기타 전형일 때만 입력)")
        @Valid
        List<@NotNull EtcScheduleCreateRequest> etcSchedules,

        @Schema(description = "면접 일정 리스트 (면접 전형일 때만 입력)")
        @Valid
        List<@NotNull InterviewScheduleCreateRequest> interviewSchedules,

        @Schema(description = "지원서 최종 상태 (지원 종료 일 때만 입력)", example = "FINAL_PASS | FINAL_FAIL")
        ApplicationStatus finalApplicationStatus
) {

    public NewStage toNewStage() {
        return NewStage.builder()
                .stageType(stageType)
                .finalApplicationStatus(finalApplicationStatus)
                .newEtcSchedules(toNewEtcSchedules())
                .newInterviewSchedules(toNewInterviewSchedules())
                .build();
    }

    private List<NewEtcSchedule> toNewEtcSchedules() {
        if (etcSchedules == null) {
            return List.of();
        }
        return etcSchedules.stream()
                .map(EtcScheduleCreateRequest:: toNewEtcSchedule)
                .toList();
    }

    private List<NewInterviewSchedule> toNewInterviewSchedules() {
        if (interviewSchedules == null) {
            return List.of();
        }
        return interviewSchedules.stream()
                .map(InterviewScheduleCreateRequest::toNewInterviewSchedule)
                .toList();
    }

}