package org.squad.careerhub.domain.schedule.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.enums.InterviewResult;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@Schema(description = "면접 일정 페이지 응답 DTO (커서 기반 페이지네이션)")
@Builder
public record InterviewSchedulePageResponse(

    @Schema(description = "면접 일정 요약 목록")
    List<InterviewScheduleResponse> interviews,

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    boolean hasNext,

    @Schema(
        description = "다음 페이지 조회에 사용할 커서 ID (마지막 면접 ID 등)",
        example = "15"
    )
    Long nextCursorId
) {

    public static InterviewSchedulePageResponse of(
        List<InterviewScheduleResponse> interviews,
        boolean hasNext,
        Long nextCursorId
    ) {
        return InterviewSchedulePageResponse.builder()
            .interviews(interviews)
            .hasNext(hasNext)
            .nextCursorId(nextCursorId)
            .build();
    }

    public static InterviewSchedulePageResponse mock() {
        InterviewScheduleResponse interview1 = InterviewScheduleResponse.builder()
            .id(10L)
            .applicationId(1L)
            .type(InterviewType.TECH)
            .datetime(LocalDateTime.parse("2025-12-10T19:00:00"))
            .location("서울 강남구 OO빌딩 3층 회의실")
            .onlineLink("https://zoom.us/j/123456789")
            .result(InterviewResult.WAITING)
            .createdAt(LocalDateTime.parse("2025-11-30T21:10:00"))
            .updatedAt(LocalDateTime.parse("2025-11-30T21:10:00"))
            .build();

        InterviewScheduleResponse interview2 = InterviewScheduleResponse.builder()
            .id(11L)
            .applicationId(1L)
            .type(InterviewType.ETC)
            .typeDetail("대표님 면접")
            .datetime(LocalDateTime.parse("2025-12-15T15:00:00"))
            .location("온라인")
            .onlineLink("https://meet.google.com/abcd-efgh-ijk")
            .result(InterviewResult.WAITING)
            .createdAt(LocalDateTime.parse("2025-11-30T21:20:00"))
            .updatedAt(LocalDateTime.parse("2025-11-30T21:20:00"))
            .build();

        return InterviewSchedulePageResponse.builder()
            .interviews(List.of(interview1, interview2))
            .hasNext(true)
            .nextCursorId(11L)
            .build();
    }
}
