package org.squad.careerhub.domain.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(description = "서류 전형 정보 DTO")
@Builder
public record DocsStage(
        @Schema(description = "마감일")
        LocalDateTime deadline,

        @Schema(description = "지원 방법", example = "온라인 제출")
        String applicationMethod
) {

}