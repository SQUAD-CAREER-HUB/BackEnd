package org.squad.careerhub.domain.jobposting.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;

@Schema(description = "채용 공고 정보 추출 응답 DTO (Controller 레이어)")
@Builder
public record JobPostingExtractResponse(

    @Schema(description = "검색한 공고 URL", example = "https://www.wanted.co.kr/wd/323219")
    String url,


    @Schema(description = "회사명", example = "에어스메디컬")
    String company,

    @Schema(description = "직무명", example = "[SwiftSight] AI Research Scientist")
    String position,

    @Schema(
        description = "공고 마감일 (상시채용 등 날짜 없으면 null)",
        example = "2025-11-20 23:59:59"
    )
    String deadline,

    @Schema(
        description = "근무지 (근무지 없으면 '-'')",
        example = "경기 성남시"
    )
    String workplace,

    @Schema(
        description = "채용 전형 단계 목록",
        example = "[\"서류\", \"1차 실무 면접\", \"2차 컬처핏\", \"최종 면접\"]"
    )
    List<String> recruitmentProcess,


    @Schema(
        description = "AI 추출 상태 (SUCCESS: 전체 추출 성공, PARTIAL: 일부만 추출, FAILED: 추출 실패)",
        example = "PARTIAL"
    )
    JobPostingExtractStatus status
) {

    /**
     * Swagger 예시 응답용 샘플 데이터
     * (원티드 공고: https://www.wanted.co.kr/wd/323219 기준)
     */
    public static JobPostingExtractResponse mock() {
        return JobPostingExtractResponse.builder()
            .url("https://www.wanted.co.kr/wd/323219")
            .company("에어스메디컬")
            .position("[SwiftSight] AI Research Scientist")
            .deadline(null)
            .workplace("경기 성남시")
            .recruitmentProcess(List.of())
            .status(JobPostingExtractStatus.PARTIAL)
            .build();
    }
}