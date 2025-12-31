package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.squad.careerhub.domain.application.service.dto.UpdateApplication;

@Schema(description = "지원서 기본 정보 수정 요청 DTO")
@Builder
public record ApplicationUpdateRequest(
        @Schema(description = "잡 포스팅 URL", example = "https://www.example.com/job-posting")
        String jobPostingUrl,

        @Schema(description = "회사 이름", example = "Naver")
        @NotBlank(message = "회사 이름은 필수 입력 항목입니다.")
        String company,

        @Schema(description = "포지션", example = "Backend Developer")
        @NotBlank(message = "포지션은 필수 입력 항목입니다.")
        String position,

        @Schema(description = "근무지", example = "Seoul, Korea")
        @NotBlank(message = "근무지는 필수 입력 항목입니다.")
        String jobLocation,

        @Schema(description = "메모", example = "This is a memo.")
        String memo
) {

    public UpdateApplication toUpdateApplication(Long applicationId) {
        return UpdateApplication.builder()
                .applicationId(applicationId)
                .jobPostingUrl(jobPostingUrl)
                .company(company)
                .position(position)
                .jobLocation(jobLocation)
                .memo(memo)
                .build();
    }

}