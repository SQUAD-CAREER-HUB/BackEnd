package org.squad.careerhub.domain.schedule.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

@Builder
public record ScheduleItemResponse(
    Long id,
    StageType stageType,
    Long applicationId,
    String companyName,

    /**
     * ETC(기타 전형)일 때만 값이 존재
     * DOCUMENT/INTERVIEW/APPLICATION_CLOSE 는 null
     */
    String stageName,
    InterviewType interviewType,
    String typeDetail,
    LocalDateTime datetime,
    String location,
    String link,
    ScheduleResult scheduleResult,
    ApplicationStatus applicationStatus,

    /**
     * DOCUMENT(서류 전형)일 때만 값이 존재
     * 그 외는 null
     */
    SubmissionStatus submissionStatus
) {

    public static ScheduleItemResponse mockEtc() {
        return ScheduleItemResponse.builder()
            .id(3L)
            .stageType(StageType.ETC)
            .applicationId(103L)
            .companyName("네이버")
            .stageName("온라인 코딩 테스트") // ETC만 존재
            .interviewType(InterviewType.TEST)
            .typeDetail(null)
            .datetime(LocalDateTime.parse("2025-12-13T14:00:00"))
            .location(null)
            .link("https://example.com/coding-test")
            .scheduleResult(ScheduleResult.WAITING)
            .build();
    }

    public static ScheduleItemResponse mockDocument() {
        return ScheduleItemResponse.builder()
            .id(1L)
            .stageType(StageType.DOCUMENT)
            .applicationId(101L)
            .companyName("삼성전자")
            .datetime(LocalDateTime.parse("2025-12-05T09:00:00"))
            .link("https://example.com/job-posting/101")
            .scheduleResult(ScheduleResult.PASS)
            .submissionStatus(SubmissionStatus.SUBMITTED) // DOCUMENT만 존재
            .build();
    }

    public static ScheduleItemResponse mockInterview() {
        return ScheduleItemResponse.builder()
            .id(2L)
            .stageType(StageType.INTERVIEW)
            .applicationId(102L)
            .companyName("당근마켓")
            .interviewType(InterviewType.TECH)
            .typeDetail("1차 화상 면접")
            .datetime(LocalDateTime.parse("2025-12-06T14:00:00"))
            .location("온라인")
            .link("https://zoom.us/j/123456789")
            .scheduleResult(ScheduleResult.WAITING)
            .build();
    }

    public static ScheduleItemResponse mockCloseFinalPass() {
        return ScheduleItemResponse.builder()
            .id(4L)
            .stageType(StageType.APPLICATION_CLOSE)
            .applicationId(104L)
            .companyName("크래프톤")
            .datetime(LocalDateTime.parse("2025-12-29T10:00:00"))
            .scheduleResult(ScheduleResult.PASS)
            .applicationStatus(ApplicationStatus.FINAL_PASS)
            .build();
    }
}
