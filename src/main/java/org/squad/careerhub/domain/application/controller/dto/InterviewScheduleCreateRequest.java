package org.squad.careerhub.domain.application.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.domain.schedule.service.dto.ApplicationInfo;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

@Schema(description = "면접 일정 생성 요청 DTO")
@Builder
public record InterviewScheduleCreateRequest(

    @Schema(
        description = "면접 유형",
        example = "TECH",
        allowableValues = {"TECH", "FIT", "EXECUTIVE", "TASK", "TEST", "ETC"},
        implementation = InterviewType.class
    )
    @NotNull(message = "면접 유형은 필수 값입니다.")
    InterviewType type,

    @Schema(
        description = "면접 유형이 ETC인 경우, 자유로운 이름 지정",
        example = "1차(기술+인성 면접)"
    )
    String typeDetail,

    @Schema(
        description = "면접 일시 (ISO8601, LocalDateTime)",
        example = "2025-12-10T19:00:00"
    )
    @NotNull(message = "면접 일시는 필수 값입니다.")
    LocalDateTime scheduledAt,

    @Schema(description = "면접 장소 (온라인일 경우 '온라인 면접 링크 입력')", example = "서울 강남구 OO 빌딩 3층 회의실 | https://zoom.us/j/123456789")
    @NotBlank(message = "면접 장소는 필수 값입니다.")
    String location,

    @Schema(
        description = "면접 링크(화상면접 등)",
        example = "https://zoom.us/..."
    )
    @Size(max = 512) String link

) {

    public NewInterviewSchedule toNewInterviewSchedule() {
        return NewInterviewSchedule.builder()
            .stageType(StageType.INTERVIEW)
            .interviewType(type)
            .typeDetail(typeDetail)
            .scheduledAt(scheduledAt)
            .location(location)
            .link(link)
            .build();
    }
}
