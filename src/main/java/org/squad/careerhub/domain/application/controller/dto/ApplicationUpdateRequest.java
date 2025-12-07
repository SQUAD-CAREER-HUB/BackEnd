package org.squad.careerhub.domain.application.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;

@Schema(description = "지원서 수정 요청 DTO")
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

        @Schema(description = "지원서 상태", example = "DOCUMENT_SUBMITTED")
        @NotNull(message = "지원서 상태는 필수 입력 항목입니다.")
        ApplicationStatus applicationStatus,

        @Schema(description = "제출일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        @JsonFormat(pattern = "yyyy.MM.dd")
        @NotNull(message = "마감일은 필수 입력 항목입니다.")
        LocalDate deadline,

        @Schema(description = "제출일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDate submittedAt,

        @Schema(description = "지원 방법", example = "Online Application")
        @NotBlank(message = "지원 방법은 필수 입력 항목입니다.")
        String applicationMethod,

        @Schema(description = "메모", example = "This is a memo.")
        String memo

) {

}