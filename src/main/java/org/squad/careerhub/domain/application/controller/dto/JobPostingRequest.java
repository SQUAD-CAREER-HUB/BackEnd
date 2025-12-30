package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;

@Schema(description = "채용공고 정보 요청 DTO")
@Builder
public record JobPostingRequest(
        @Schema(description = "채용공고 URL (직접 입력 시 null)", example = "https://www.example.com/job-posting")
        String jobPostingUrl,

        @Schema(description = "회사 이름", example = "Naver")
        @NotBlank(message = "회사 이름은 필수 입력 항목입니다.")
        String company,

        @Schema(description = "포지션", example = "Backend Developer")
        @NotBlank(message = "포지션은 필수 입력 항목입니다.")
        String position,

        @Schema(description = "마감일")
        @NotNull(message = "마감일은 필수 입력 항목입니다.")
        LocalDateTime deadline,

        @Schema(description = "근무지", example = "Seoul, Korea")
        String jobLocation
) {

}