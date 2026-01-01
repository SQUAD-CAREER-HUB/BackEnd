package org.squad.careerhub.domain.application;

import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.time.LocalDateTime;
import java.util.List;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.service.dto.NewDocsSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

public final class ApplicationFixture {

    public static Application createApplicationDocs(Member author) {
        return createApplication(author, StageType.DOCUMENT, ApplicationStatus.IN_PROGRESS);
    }

    public static Application createApplicationInterview(Member author) {
        return createApplication(author, StageType.INTERVIEW, ApplicationStatus.IN_PROGRESS);
    }

    public static Application createApplicationEtc(Member author) {
        return createApplication(author, StageType.ETC, ApplicationStatus.IN_PROGRESS);
    }

    public static Application createApplicationClosed(Member author, ApplicationStatus applicationStatus) {
        return createApplication(author, StageType.APPLICATION_CLOSE, applicationStatus);
    }

    public static Application createApplication(
            Member author,
            StageType stageType,
            ApplicationStatus applicationStatus
    ) {
        return Application.create(
                author,
                "http://jobposting.url",
                "CompanyName",
                "PositionName",
                "JobLocation",
                stageType,
                applicationStatus,
                ApplicationMethod.EMAIL,
                now()
        );
    }

    // NewApplication 생성 헬퍼
    public static NewApplication createNewApplication(StageType stageType, ApplicationStatus status) {
        return createNewApplication("Naver", "BE", stageType, status, now().plusDays(10));
    }

    public static NewApplication createNewApplication(
            String company,
            String position,
            StageType stageType,
            ApplicationStatus status,
            LocalDateTime deadline
    ) {
        return createNewApplication(
                "https://www.careerhub.com/job/12345",
                company,
                position,
                "서울 강남구",
                deadline,
                stageType,
                ApplicationMethod.EMAIL,
                status
        );
    }

    private static NewApplication createNewApplication(
            String jobPostingUrl,
            String company,
            String position,
            String jobLocation,
            LocalDateTime deadline,
            StageType stageType,
            ApplicationMethod applicationMethod,
            ApplicationStatus finalApplicationStatus
    ) {
        return NewApplication.builder()
                .jobPostingUrl(jobPostingUrl)
                .company(company)
                .position(position)
                .jobLocation(jobLocation)
                .deadline(deadline)
                .stageType(stageType)
                .applicationMethod(applicationMethod)
                .finalApplicationStatus(finalApplicationStatus)
                .build();
    }

    // NewStage 생성 헬퍼
    public static NewStage createDocumentStage(SubmissionStatus submissionStatus, ScheduleResult scheduleResult) {
        return NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .newDocsSchedule(new NewDocsSchedule(submissionStatus, scheduleResult))
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();
    }

    public static NewStage createEtcStageWithCustomSchedule(NewEtcSchedule etcSchedule) {
        return NewStage.builder()
                .stageType(StageType.ETC)
                .newEtcSchedules(List.of(etcSchedule))
                .newInterviewSchedules(List.of())
                .build();
    }

    public static NewStage createInterviewStage(LocalDateTime startedAt, String location) {
        return NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of(
                        new NewInterviewSchedule("기술 면접", startedAt, location, ScheduleResult.WAITING)
                ))
                .build();
    }

    public static NewStage createInterviewStageWithCustomSchedule(NewInterviewSchedule interviewSchedule) {
        return NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of(interviewSchedule))
                .build();
    }

    public static NewStage createApplicationCloseStage() {
        return NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .newEtcSchedules(List.of())
                .newInterviewSchedules(List.of())
                .build();
    }

    // NewSchedule 생성 헬퍼
    public static NewEtcSchedule createEtcSchedule(String scheduleName, LocalDateTime startedAt, LocalDateTime endedAt) {
        return new NewEtcSchedule(scheduleName, startedAt, endedAt, ScheduleResult.WAITING);
    }

    public static NewInterviewSchedule createInterviewSchedule(String scheduleName, LocalDateTime startedAt, String location) {
        return new NewInterviewSchedule(scheduleName, startedAt, location, ScheduleResult.WAITING);
    }

}