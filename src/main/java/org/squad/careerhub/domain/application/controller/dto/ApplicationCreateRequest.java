package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;

@Schema(description = "지원서 생성 요청 DTO")
@Builder
public record ApplicationCreateRequest(
        @Schema(description = "채용공고 정보")
        @NotNull(message = "채용공고 정보는 필수 입력 항목입니다.")
        @Valid
        JobPostingRequest jobPosting,

        @Schema(description = "지원 정보")
        @NotNull(message = "지원 정보는 필수 입력 항목입니다.")
        @Valid
        ApplicationInfoRequest applicationInfo,

        @Schema(description = "전형 단계 정보")
        @NotNull(message = "전형 단계 정보는 필수 입력 항목입니다.")
        @Valid
        StageRequest stage
) {

    public NewJobPosting toNewJobPosting() {
        return jobPosting.toNewJobPosting();
    }

    public NewApplicationInfo toNewApplicationInfo() {
        return applicationInfo.toNewApplicationInfo();
    }

    public NewStage toNewStage() {
        return stage.toNewStage();
    }

}