package org.squad.careerhub.domain.schedule.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Schema(description = "캘린더 일정 통합 조회 응답")
@Builder
public record ScheduleListResponse(

    @Schema(description = "조회 시작일", example = "2025-12-01")
    LocalDate from,

    @Schema(description = "조회 종료일", example = "2025-12-31")
    LocalDate to,

    @Schema(description = "통합 일정 목록(datetime 오름차순)")
    List<ScheduleItemResponse> items
) {

    public static ScheduleListResponse mock() {
        return ScheduleListResponse.builder()
            .from(LocalDate.parse("2025-12-01"))
            .to(LocalDate.parse("2025-12-31"))
            .items(List.of(
                ScheduleItemResponse.mockDocument(),
                ScheduleItemResponse.mockInterview(),
                ScheduleItemResponse.mockEtc(),
                ScheduleItemResponse.mockCloseFinalPass()
            ))
            .build();
    }
}
