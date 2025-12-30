package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;

@Builder
public record ScheduleItemResponse(
        Long id,
        StageType stageType,
        Long applicationId,
        String companyName,
        String scheduleName,
        String location,
        LocalDateTime startedAt,
        ScheduleResult scheduleResult,
        ApplicationStatus applicationStatus,
        SubmissionStatus submissionStatus
) {

    public static ScheduleItemResponse mockEtc() {
        return ScheduleItemResponse.builder()
                .id(3L)
                .stageType(StageType.ETC)
                .applicationId(103L)
                .companyName("네이버")
                .scheduleName("온라인 코딩 테스트") // ETC만 존재
                .startedAt(LocalDateTime.parse("2025-12-13T14:00:00"))
                .location(null)
                .scheduleResult(
                        org.squad.careerhub.domain.application.entity.ScheduleResult.WAITING)
                .build();
    }

    public static ScheduleItemResponse mockDocument() {
        return ScheduleItemResponse.builder()
                .id(1L)
                .stageType(StageType.DOCUMENT)
                .applicationId(101L)
                .companyName("삼성전자")
                .startedAt(LocalDateTime.parse("2025-12-05T09:00:00"))
                .scheduleResult(org.squad.careerhub.domain.application.entity.ScheduleResult.PASS)
                .submissionStatus(SubmissionStatus.SUBMITTED) // DOCUMENT만 존재
                .build();
    }

    public static ScheduleItemResponse mockInterview() {
        return ScheduleItemResponse.builder()
                .id(2L)
                .stageType(StageType.INTERVIEW)
                .applicationId(102L)
                .companyName("당근마켓")
                .scheduleName("1차 면접")
                .startedAt(LocalDateTime.parse("2025-12-06T14:00:00"))
                .location("온라인")
                .scheduleResult(
                        org.squad.careerhub.domain.application.entity.ScheduleResult.WAITING)
                .build();
    }

    public static ScheduleItemResponse mockCloseFinalPass() {
        return ScheduleItemResponse.builder()
                .id(4L)
                .stageType(StageType.APPLICATION_CLOSE)
                .applicationId(104L)
                .companyName("크래프톤")
                .startedAt(LocalDateTime.parse("2025-12-29T10:00:00"))
                .scheduleResult(org.squad.careerhub.domain.application.entity.ScheduleResult.PASS)
                .applicationStatus(ApplicationStatus.FINAL_PASS)
                .build();
    }
}
