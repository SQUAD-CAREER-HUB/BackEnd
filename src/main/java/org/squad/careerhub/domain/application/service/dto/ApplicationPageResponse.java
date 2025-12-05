package org.squad.careerhub.domain.application.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;

@Schema(description = "지원서 페이지 응답 DTO")
@Builder
public record ApplicationPageResponse(
        @Schema(description = "지원서 요약 목록")
        List<ApplicationSummaryResponse> applications,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "다음 커서 ID", example = "15")
        Long nextCursorId
) {

    public static ApplicationPageResponse mock() {
        ApplicationSummaryResponse application1 = ApplicationSummaryResponse.builder()
                .applicationId(1L)
                .company("Naver")
                .position("Backend Developer")
                .applicationStatus(ApplicationStatus.DOCUMENT_SUBMITTED.getDescription())
                .deadline(LocalDate.of(2025, 3, 25))
                .submittedAt(LocalDate.of(2025, 3, 20))
                .nextInterviewDate(LocalDate.of(2025, 4, 1))
                .build();

        ApplicationSummaryResponse application2 = ApplicationSummaryResponse.builder()
                .applicationId(2L)
                .company("Kakao")
                .position("Backend Developer")
                .applicationStatus(ApplicationStatus.INTERVIEW_SCHEDULED.getDescription())
                .deadline(LocalDate.of(2025, 4, 10))
                .submittedAt(LocalDate.of(2025, 3, 28))
                .nextInterviewDate(LocalDate.of(2025, 4, 1))
                .build();

        return ApplicationPageResponse.builder()
                .applications(List.of(application1, application2))
                .hasNext(true)
                .nextCursorId(2L)
                .build();
    }

    public static ApplicationPageResponse inProgressMock() {
        ApplicationSummaryResponse application1 = ApplicationSummaryResponse.builder()
                .applicationId(1L)
                .company("Naver")
                .position("Backend Developer")
                .applicationStatus(ApplicationStatus.DOCUMENT_SUBMITTED.getDescription())
                .deadline(LocalDate.of(2025, 3, 25))
                .submittedAt(LocalDate.of(2025, 3, 20))
                .build();

        ApplicationSummaryResponse application2 = ApplicationSummaryResponse.builder()
                .applicationId(2L)
                .company("Kakao")
                .position("Backend Developer")
                .applicationStatus(ApplicationStatus.INTERVIEW_SCHEDULED.getDescription())
                .deadline(LocalDate.of(2025, 4, 10))
                .submittedAt(LocalDate.of(2025, 3, 28))
                .build();

        return ApplicationPageResponse.builder()
                .applications(List.of(application1, application2))
                .hasNext(true)
                .nextCursorId(2L)
                .build();
    }
}