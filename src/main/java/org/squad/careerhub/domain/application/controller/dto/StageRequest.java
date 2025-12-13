package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

@Schema(description = "전형 단계 요청 DTO")
@Builder
public record StageRequest(
        @Schema(description = "전형 단계", example = "DOCUMENT")
        @NotNull(message = "전형 단계는 필수 입력 항목입니다.")
        StageType stageType,

        @Schema(description = "서류 제출 상태 (서류 전형일 때만 입력)", example = "NOT_SUBMITTED")
        SubmissionStatus submissionStatus,

        @Schema(description = "기타 전형 일정 (기타 전형일 때만 입력)")
        @Valid
        EtcScheduleCreateRequest etcSchedule,

        @Schema(description = "면접 일정 리스트 (면접 전형일 때만 입력)")
        @Valid
        List<InterviewScheduleCreateRequest> interviewSchedules
) {

    public NewStage toNewStage() {
        return NewStage.builder()
                .stageType(stageType)
                .submissionStatus(submissionStatus)
                .newEtcSchedule(toNewEtcSchedule())
                .newInterviewSchedules(toNewInterviewSchedules())
                .build();
    }

    private NewEtcSchedule toNewEtcSchedule() {
        if (etcSchedule == null) {
            return NewEtcSchedule.builder()
                    .stageName(StageType.ETC.getDescription())
                    .build();
        }
        return etcSchedule.toNewEtcSchedule();
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

