package org.squad.careerhub.domain.schedule.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.service.dto.ScheduleItem;

@Schema(description = "캘린더 일정 통합 조회 응답")
@Builder
public record ScheduleListResponse(
        @Schema(description = "통합 일정 목록")
        List<ScheduleItem> items
) {

    public static ScheduleListResponse from(List<ScheduleItem> schedules) {
        return new ScheduleListResponse(schedules);
    }
}
