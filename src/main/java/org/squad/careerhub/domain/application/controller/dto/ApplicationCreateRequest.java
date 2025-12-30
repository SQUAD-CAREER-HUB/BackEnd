package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.NewStage;

@Schema(description = "지원서 생성 요청 DTO")
@Builder
public record ApplicationCreateRequest(
        @Schema(description = "채용공고 정보")
        @NotNull(message = "채용공고 정보는 필수 입력 항목입니다.")
        @Valid
        JobPostingRequest jobPosting,

        @Schema(description = "전형 단계 정보")
        @NotNull(message = "전형 단계 정보는 필수 입력 항목입니다.")
        @Valid
        StageRequest stage
) {

    public NewApplication toNewApplication() {
        return NewApplication.builder()
                .jobPostingUrl(jobPosting().jobPostingUrl())
                .company(jobPosting().company())
                .position(jobPosting().position())
                .deadline(jobPosting.deadline())
                .jobLocation(jobPosting().jobLocation())
                .stageType(stage.stageType())
                .applicationMethod(stage.docsStageCreateRequest().applicationMethod())
                .finalApplicationStatus(stage.finalApplicationStatus())
                .build();
    }

    public NewStage toNewStage() {
        return NewStage.builder()
                .stageType(stage.stageType())
                .finalApplicationStatus(stage.finalApplicationStatus())
                .newEtcSchedules(stage.toNewStage().newEtcSchedules())
                .newInterviewSchedules(stage.toNewStage().newInterviewSchedules())
                .build();
    }

}