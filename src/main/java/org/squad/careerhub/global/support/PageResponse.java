package org.squad.careerhub.global.support;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;

@Schema(description = "페이지 응답 DTO")
@Builder
public record PageResponse<T>(
        @Schema(description = "페이지 내용")
        List<T> contents,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "다음 커서 ID", example = "123")
        Long nextCursorId
) {

    public static PageResponse<ApplicationSummaryResponse> mock() {
        return new PageResponse<>(
                List.of(),
                false,
                null
        );
    }

}