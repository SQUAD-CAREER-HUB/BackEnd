package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "지원서 상세 페이지 응답 DTO")
@Builder
public record ApplicationDetailPageResponse(
        ApplicationInfoResponse applicationInfo,
        ApplicationStageTimeLineListResponse applicationStageTimeLine
) {

}