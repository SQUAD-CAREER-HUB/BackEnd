package org.squad.careerhub.domain.schedule.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

@Schema(description = "면접 일정 생성 요청 DTO")
@Builder
public record InterviewScheduleCreateRequest(

        @Schema(description = "면접 이름 (예: 1차 실무 면접)", example = "1차 실무 면접")
        @NotBlank(message = "면접 이름은 필수 값입니다.")
        String name,

        @Schema(
                description = "면접 유형",
                example = "TECH",
                allowableValues = {"TECH", "FIT", "EXECUTIVE", "TASK", "TEST", "ETC"},
                implementation = InterviewType.class
        )
        @NotNull(message = "면접 유형은 필수 값입니다.")
        InterviewType type,

        @Schema(
                description = "면접 일시 (ISO8601, LocalDateTime)",
                example = "2025-12-10T19:00:00"
        )
        @NotNull(message = "면접 일시는 필수 값입니다.")
        LocalDateTime scheduledAt,

        @Schema(description = "면접 장소 (온라인일 경우 '온라인 면접 링크 입력')", example = "서울 강남구 OO 빌딩 3층 회의실 | https://zoom.us/j/123456789")
        @NotBlank(message = "면접 장소는 필수 값입니다.")
        String location
) {

    public NewInterviewSchedule toNewInterviewSchedule() {
        return NewInterviewSchedule.builder()
                .stageName(name)
                .type(type)
                .scheduledAt(scheduledAt)
                .location(location)
                .build();
    }

}