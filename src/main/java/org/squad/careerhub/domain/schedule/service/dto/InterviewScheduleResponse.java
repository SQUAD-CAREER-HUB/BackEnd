package org.squad.careerhub.domain.schedule.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@Schema(description = "면접 일정 응답 DTO")
@Builder
public record InterviewScheduleResponse(

    @Schema(description = "면접 ID", example = "10")
    Long id,

    @Schema(description = "지원 카드 ID", example = "1")
    Long applicationId,

    @Schema(
        description = "면접 유형",
        example = "TECH",
        allowableValues = {"TECH", "FIT", "EXECUTIVE", "DESIGN", "TEST", "OTHER"}
    )
    InterviewType type,

    @Schema(
        description = "기타 면접 유형 상세 (type= OTHER 일 때만 사용)",
        example = "인성 + 기술 면접"
    )
    String typeDetail,

    @Schema(
        description = "면접 일시 (ISO8601, LocalDateTime)",
        example = "2025-12-10T19:00:00"
    )
    LocalDateTime datetime,

    @Schema(description = "면접 장소", example = "서울 강남구 OO빌딩 3층 회의실")
    String location,

    @Schema(
        description = "온라인 면접 링크 (온라인일 경우)",
        example = "https://zoom.us/j/123456789"
    )
    String onlineLink,

    @Schema(
        description = "면접 상태",
        example = "SCHEDULED",
        allowableValues = {"SCHEDULED", "DONE", "CANCELED", "NONAPPEARANCE"}
    )
    String status,

    @Schema(description = "생성 시각", example = "2025-11-30T21:10:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 시각", example = "2025-11-30T21:20:00")
    LocalDateTime updatedAt
) {

    public static InterviewScheduleResponse of(
        Long id,
        Long applicationId,
        InterviewType type,
        String typeDetail,
        LocalDateTime datetime,
        String location,
        String onlineLink,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return InterviewScheduleResponse.builder()
            .id(id)
            .applicationId(applicationId)
            .type(type)
            .typeDetail(typeDetail)
            .datetime(datetime)
            .location(location)
            .onlineLink(onlineLink)
            .status(status)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }

    public static InterviewScheduleResponse mock() {
        return InterviewScheduleResponse.builder()
            .id(10L)
            .applicationId(1L)
            .type(InterviewType.TECH)
            .datetime(LocalDateTime.parse("2025-12-10T19:00:00"))
            .location("서울 강남구 OO빌딩 3층 회의실")
            .onlineLink("https://zoom.us/j/123456789")
            .status("SCHEDULED")
            .createdAt(LocalDateTime.parse("2025-11-30T21:10:00"))
            .updatedAt(LocalDateTime.parse("2025-11-30T21:10:00"))
            .build();
    }
}

