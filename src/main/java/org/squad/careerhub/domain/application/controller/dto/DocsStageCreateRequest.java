package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;

@Schema(description = "서류 전형 생성 요청 DTO")
@Builder
public record DocsStageCreateRequest(
        @Schema(description = "제출 상태", example = "SUBMITTED")
        @NotNull(message = "제출 상태는 필수 값입니다.")
        SubmissionStatus submissionStatus,

        @Schema(description = "지원 방법", example = "ONLINE")
        @NotNull(message = "지원 방법은 필수 값입니다.")
        ApplicationMethod applicationMethod,

        @Schema(description = "전형 결과", example = "PASS")
        @NotNull(message = "전형 결과는 필수 값입니다.")
        ScheduleResult scheduleResult
) {

}