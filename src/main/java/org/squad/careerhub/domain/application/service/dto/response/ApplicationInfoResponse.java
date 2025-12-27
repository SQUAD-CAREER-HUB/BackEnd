package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;

@Schema(description = "지원서 정보 응답 DTO")
@Builder
public record ApplicationInfoResponse(
        @Schema(description = "지원서 ID", example = "1")
        Long applicationId,

        @Schema(description = "회사 이름", example = "Naver")
        String company,

        @Schema(description = "포지션", example = "Backend Developer")
        String position,

        @Schema(description = "근무지", example = "Seoul, Korea")
        String jobLocation,

        @Schema(description = "잡 포스팅 URL - 없을 경우 다른 값 전달.", example = "https://www.example.com/job-posting")
        String jobPostingUrl,

        @Schema(description = "지원서 전형 단계", example = "서류 전형")
        String currentStageType,

        @Schema(description = "지원서 상태", example = "FINAL_PASS")
        String applicationStatus,

        @Schema(description = "마감일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        LocalDateTime deadline,

        @Schema(description = "지원 방법", example = "Online Application")
        String applicationMethod,

        @Schema(description = "메모", example = "This is a memo.")
        String memo,

        @Schema(description = "첨부 파일 목록")
        List<String> attachedFiles
) {

    public static ApplicationInfoResponse of(Application application, List<ApplicationAttachment> attachments) {
        return ApplicationInfoResponse.builder()
                .applicationId(application.getId())
                .company(application.getCompany())
                .position(application.getPosition())
                .jobLocation(application.getJobLocation())
                .jobPostingUrl(application.getJobPostingUrl() != null ? application.getJobPostingUrl() : "N/A")
                .currentStageType(application.getCurrentStageType().getDescription())
                .applicationStatus(application.getApplicationStatus().name())
                .deadline(application.getDeadline())
                .applicationMethod(application.getApplicationMethod().getDescription())
                .memo(application.getMemo())
                .attachedFiles(attachments.stream()
                        .map(ApplicationAttachment::getFileUrl)
                        .toList())
                .build();
    }

}
