package org.squad.careerhub.domain.community.interviewreview.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "면접 후기 신고 요청 DTO")
@Builder
public record ReviewReportRequest(
        @Schema(description = "신고 사유", example = "부적절한 내용 포함")
        @NotBlank(message = "신고 사유는 필수 입력 항목입니다.")
        String reason

) {

}
