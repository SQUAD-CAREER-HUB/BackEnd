package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;

@Schema(description = "지원서 상세 응답 DTO")
@Builder
public record ApplicationDetailResponse(
        @Schema(description = "지원서 ID", example = "1")
        Long applicationId,

        @Schema(description = "잡 포스팅 URL - 없을 경우 다른 값 전달.", example = "https://www.example.com/job-posting")
        String jobPostingUrl,

        @Schema(description = "회사 이름", example = "Naver")
        String company,

        @Schema(description = "포지션", example = "Backend Developer")
        String position,

        @Schema(description = "근무지", example = "Seoul, Korea")
        String jobLocation,

        @Schema(description = "지원서 상태", example = "서류 제출 완료")
        String applicationStatus,

        @Schema(description = "제출일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        LocalDate deadline,

        @Schema(description = "제출일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        LocalDate submittedAt,

        @Schema(description = "지원 방법", example = "Online Application")
        String applicationMethod,

        @Schema(description = "메모", example = "This is a memo.")
        String memo,

        @Schema(description = "첨부 파일 목록")
        List<String> attachedFiles
) {

    public static ApplicationDetailResponse mock() {
        return ApplicationDetailResponse.builder()
                .applicationId(1L)
                .jobPostingUrl("https://www.example.com/job-posting")
                .company("Naver")
                .position("Backend Developer")
                .jobLocation("Seoul, Korea")
                .applicationStatus(ApplicationStatus.DOCUMENT_SUBMITTED.getDescription())
                .deadline(LocalDate.of(2025, 3, 25))
                .submittedAt(LocalDate.of(2025, 3, 20))
                .applicationMethod("Online Application")
                .memo("This is a memo.")
                .attachedFiles(List.of("resume.pdf", "cover-letter.pdf"))
                .build();
    }

}