package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationInfoResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStageTimeLineListResponse.EtcStageTimeLine;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStageTimeLineListResponse.InterviewStageTimeLine;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.service.MemberManager;
import org.squad.careerhub.domain.schedule.entity.Schedule;
import org.squad.careerhub.domain.schedule.repository.ScheduleJpaRepository;

@RequiredArgsConstructor
@Transactional
class FindApplicationIntegrationTest extends IntegrationTestSupport {

    final ApplicationManager applicationManager;
    final ApplicationReader applicationReader;
    final ApplicationStageJpaRepository applicationStageJpaRepository;
    final MemberManager memberManager;
    final ScheduleJpaRepository scheduleJpaRepository;
    final ApplicationAttachmentJpaRepository applicationAttachmentJpaRepository;

    Member member;

    @BeforeEach
    void setUp() {
        member = memberManager.create(Member.create(
                "email",
                SocialProvider.KAKAO,
                "socialId",
                "nickname",
                "profileImageUrl"
        ));
    }

    @Test
    void 지원서_상세_페이지_조회를_위한_서류_전형만_존재하는_전형_타임_라인_정보와_지원서_정보를_조회한다() {
        // given
        var app = createApplication(
                "네이버",
                "백엔드",
                NewStage.builder()
                        .stageType(StageType.DOCUMENT)
                        .submissionStatus(SubmissionStatus.SUBMITTED)
                        .newEtcSchedules(List.of())
                        .newInterviewSchedules(List.of())
                        .build()
        );
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.DOCUMENT));
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf")
        );
        createApplicationSchedule(
                docsStage,
                StageType.DOCUMENT.getDescription(),
                null,
                ScheduleResult.WAITING,
                SubmissionStatus.SUBMITTED,
                now(),
                app.getDeadline()
        );

        // when
        var applicationDetailPageResponse = applicationReader.findApplication(app.getId(), member.getId());

        // then
        assertThat(applicationDetailPageResponse.applicationInfo()).isNotNull()
                .extracting(
                        ApplicationInfoResponse::applicationId,
                        ApplicationInfoResponse::company,
                        ApplicationInfoResponse::position,
                        ApplicationInfoResponse::jobLocation,
                        ApplicationInfoResponse::jobPostingUrl,
                        ApplicationInfoResponse::currentStageType,
                        ApplicationInfoResponse::applicationStatus,
                        ApplicationInfoResponse::deadline,
                        ApplicationInfoResponse::applicationMethod,
                        ApplicationInfoResponse::memo,
                        ApplicationInfoResponse::attachedFiles
                ).containsExactly(
                        app.getId(),
                        app.getCompany(),
                        app.getPosition(),
                        app.getJobLocation(),
                        app.getJobPostingUrl(),
                        StageType.DOCUMENT.getDescription(),
                        app.getApplicationStatus().name(),
                        app.getDeadline(),
                        app.getApplicationMethod().getDescription(),
                        null,
                        List.of(attachment.getFileUrl())
                );
        assertThat(applicationDetailPageResponse.applicationStageTimeLine()).isNotNull()
                .extracting(
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().stageId(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleName(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleResult(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().submissionStatus()
                ).containsExactly(
                        docsStage.getId(),
                        StageType.DOCUMENT.getDescription(),
                        ScheduleResult.WAITING,
                        SubmissionStatus.SUBMITTED
                );

        var etcStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().etcStageTimeLine();
        assertThat(etcStageTimeLines).isEmpty();
        var interviewStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().interviewStageTimeLine();
        assertThat(interviewStageTimeLines).isEmpty();
    }

    @Test
    void 모든_전형이_존재하는_전형_타임_라인_정보와_지원서_정보를_조회한다() {
        // given
        var app = createApplication(
                "네이버",
                "백엔드",
                NewStage.builder()
                        .stageType(StageType.INTERVIEW)
                        .newEtcSchedules(List.of())
                        .newInterviewSchedules(List.of())
                        .build()
        );
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf"
        ));
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.DOCUMENT));
        var etcStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.ETC));
        var interviewStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.INTERVIEW));

        var docsSchedule = createApplicationSchedule(
                docsStage,
                StageType.DOCUMENT.getDescription(),
                null,
                ScheduleResult.PASS,
                SubmissionStatus.SUBMITTED,
                now(),
                app.getDeadline()
        );
        var codingTestSchedule = createApplicationSchedule(
                etcStage,
                "코딩테스트",
                null,
                ScheduleResult.PASS,
                null,
                now().plusDays(1),
                null
        );
        var studySchedule = createApplicationSchedule(
                etcStage,
                "과제전형",
                null,
                ScheduleResult.PASS,
                null,
                now().plusDays(2),
                now().plusDays(3)
        );
        var firstInterview = createApplicationSchedule(
                interviewStage,
                "1차 면접",
                "판교",
                ScheduleResult.PASS,
                null,
                now().plusDays(3),
                null
        );
        var secInterview = createApplicationSchedule(
                interviewStage,
                "2차 면접",
                "판교",
                ScheduleResult.WAITING,
                null,
                now().plusDays(5),
                null
        );

        // when
        var applicationDetailPageResponse = applicationReader.findApplication(app.getId(), member.getId());

        // then
        assertThat(applicationDetailPageResponse.applicationInfo()).isNotNull()
                .extracting(
                        ApplicationInfoResponse::applicationId,
                        ApplicationInfoResponse::company,
                        ApplicationInfoResponse::position,
                        ApplicationInfoResponse::jobLocation,
                        ApplicationInfoResponse::jobPostingUrl,
                        ApplicationInfoResponse::currentStageType,
                        ApplicationInfoResponse::applicationStatus,
                        ApplicationInfoResponse::deadline,
                        ApplicationInfoResponse::applicationMethod,
                        ApplicationInfoResponse::memo,
                        ApplicationInfoResponse::attachedFiles
                ).containsExactly(
                        app.getId(),
                        app.getCompany(),
                        app.getPosition(),
                        app.getJobLocation(),
                        app.getJobPostingUrl(),
                        StageType.INTERVIEW.getDescription(),
                        app.getApplicationStatus().name(),
                        app.getDeadline(),
                        app.getApplicationMethod().getDescription(),
                        null,
                        List.of(attachment.getFileUrl())
                );
        assertThat(applicationDetailPageResponse.applicationStageTimeLine()).isNotNull()
                .extracting(
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().stageId(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleName(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleResult(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().submissionStatus()
                ).containsExactly(
                        docsStage.getId(),
                        docsSchedule.getScheduleName(),
                        docsSchedule.getScheduleResult(),
                        docsSchedule.getSubmissionStatus()
                );

        var etcStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().etcStageTimeLine();
        assertThat(etcStageTimeLines).hasSize(2).extracting(
                EtcStageTimeLine::stageId,
                EtcStageTimeLine::scheduleName,
                EtcStageTimeLine::scheduleResult,
                EtcStageTimeLine::startedAt,
                EtcStageTimeLine::endedAt
        ).containsExactlyInAnyOrder(
                tuple(
                        etcStage.getId(),
                        codingTestSchedule.getScheduleName(),
                        codingTestSchedule.getScheduleResult(),
                        codingTestSchedule.getStartedAt(),
                        null
                ),
                tuple(
                        etcStage.getId(),
                        studySchedule.getScheduleName(),
                        studySchedule.getScheduleResult(),
                        studySchedule.getStartedAt(),
                        studySchedule.getEndedAt()
                )
        );
        var interviewStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().interviewStageTimeLine();
        assertThat(interviewStageTimeLines).hasSize(2).extracting(
                InterviewStageTimeLine::stageId,
                InterviewStageTimeLine::scheduleName,
                InterviewStageTimeLine::scheduleResult,
                InterviewStageTimeLine::location,
                InterviewStageTimeLine::startedAt
        ).containsExactlyInAnyOrder(
                tuple(
                        interviewStage.getId(),
                        firstInterview.getScheduleName(),
                        firstInterview.getScheduleResult(),
                        firstInterview.getLocation(),
                        firstInterview.getStartedAt()
                ),
                tuple(
                        interviewStage.getId(),
                        secInterview.getScheduleName(),
                        secInterview.getScheduleResult(),
                        secInterview.getLocation(),
                        secInterview.getStartedAt()
                )
        );
    }

    @Test
    void 전형은_존재하지만_일정이_없는_기타_전형인_상태의_타임_라인_정보와_지원서_정보를_조회한다() {
        // given
        var app = createApplication(
                "네이버",
                "백엔드",
                NewStage.builder()
                        .stageType(StageType.ETC)
                        .newEtcSchedules(List.of())
                        .newInterviewSchedules(List.of())
                        .build()
        );
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf"
        ));
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.DOCUMENT));
        var etcStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.ETC));

        var docsSchedule = createApplicationSchedule(
                docsStage,
                StageType.DOCUMENT.getDescription(),
                null,
                ScheduleResult.PASS,
                SubmissionStatus.SUBMITTED,
                now(),
                app.getDeadline()
        );

        // when
        var applicationDetailPageResponse = applicationReader.findApplication(app.getId(), member.getId());

        // then
        assertThat(applicationDetailPageResponse.applicationInfo()).isNotNull()
                .extracting(
                        ApplicationInfoResponse::applicationId,
                        ApplicationInfoResponse::company,
                        ApplicationInfoResponse::position,
                        ApplicationInfoResponse::jobLocation,
                        ApplicationInfoResponse::jobPostingUrl,
                        ApplicationInfoResponse::currentStageType,
                        ApplicationInfoResponse::applicationStatus,
                        ApplicationInfoResponse::deadline,
                        ApplicationInfoResponse::applicationMethod,
                        ApplicationInfoResponse::memo,
                        ApplicationInfoResponse::attachedFiles
                ).containsExactly(
                        app.getId(),
                        app.getCompany(),
                        app.getPosition(),
                        app.getJobLocation(),
                        app.getJobPostingUrl(),
                        StageType.ETC.getDescription(),
                        app.getApplicationStatus().name(),
                        app.getDeadline(),
                        app.getApplicationMethod().getDescription(),
                        null,
                        List.of(attachment.getFileUrl())
                );
        assertThat(applicationDetailPageResponse.applicationStageTimeLine()).isNotNull()
                .extracting(
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().stageId(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleName(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleResult(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().submissionStatus()
                ).containsExactly(
                        docsStage.getId(),
                        docsSchedule.getScheduleName(),
                        docsSchedule.getScheduleResult(),
                        docsSchedule.getSubmissionStatus()
                );

        var etcStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().etcStageTimeLine();
        assertThat(etcStageTimeLines).hasSize(1)
                .extracting(
                        EtcStageTimeLine::stageId,
                        EtcStageTimeLine::scheduleName,
                        EtcStageTimeLine::scheduleResult,
                        EtcStageTimeLine::startedAt,
                        EtcStageTimeLine::endedAt
                ).containsExactly(
                        tuple(
                                etcStage.getId(),
                                null,
                                null,
                                null,
                                null
                        )
                );
        var interviewStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().interviewStageTimeLine();
        assertThat(interviewStageTimeLines).isEmpty();
    }

    @Test
    void 최종합격_지원서를_조회한다() {
        // given
        var app = createApplication(
                "네이버",
                "백엔드",
                NewStage.builder()
                        .stageType(StageType.APPLICATION_CLOSE)
                        .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
                        .newEtcSchedules(List.of())
                        .newInterviewSchedules(List.of())
                        .build()
        );
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf"
        ));
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.DOCUMENT));

        var docsSchedule = createApplicationSchedule(
                docsStage,
                StageType.DOCUMENT.getDescription(),
                null,
                ScheduleResult.PASS,
                SubmissionStatus.SUBMITTED,
                now(),
                app.getDeadline()
        );
        // when
        var applicationDetailPageResponse = applicationReader.findApplication(app.getId(), member.getId());

        // then
        assertThat(applicationDetailPageResponse.applicationInfo()).isNotNull()
                .extracting(
                        ApplicationInfoResponse::applicationId,
                        ApplicationInfoResponse::company,
                        ApplicationInfoResponse::position,
                        ApplicationInfoResponse::jobLocation,
                        ApplicationInfoResponse::jobPostingUrl,
                        ApplicationInfoResponse::currentStageType,
                        ApplicationInfoResponse::applicationStatus,
                        ApplicationInfoResponse::deadline,
                        ApplicationInfoResponse::applicationMethod,
                        ApplicationInfoResponse::memo,
                        ApplicationInfoResponse::attachedFiles
                ).containsExactly(
                        app.getId(),
                        app.getCompany(),
                        app.getPosition(),
                        app.getJobLocation(),
                        app.getJobPostingUrl(),
                        StageType.APPLICATION_CLOSE.getDescription(),
                        ApplicationStatus.FINAL_PASS.name(),
                        app.getDeadline(),
                        app.getApplicationMethod().getDescription(),
                        null,
                        List.of(attachment.getFileUrl())
                );
        assertThat(applicationDetailPageResponse.applicationStageTimeLine()).isNotNull()
                .extracting(
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().stageId(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleName(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().scheduleResult(),
                        stageTimeLine -> stageTimeLine.docsStageTimeLine().submissionStatus()
                ).containsExactly(
                        docsStage.getId(),
                        docsSchedule.getScheduleName(),
                        docsSchedule.getScheduleResult(),
                        docsSchedule.getSubmissionStatus()
                );

        var etcStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().etcStageTimeLine();
        assertThat(etcStageTimeLines).isEmpty();

        var interviewStageTimeLines = applicationDetailPageResponse.applicationStageTimeLine().interviewStageTimeLine();
        assertThat(interviewStageTimeLines).isEmpty();
    }

    private Schedule createApplicationSchedule(
            ApplicationStage applicationStage,
            String scheduleName,
            String location,
            ScheduleResult scheduleResult,
            SubmissionStatus submissionStatus,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        Schedule schedule = Schedule.register(
                member,
                applicationStage,
                scheduleName,
                location,
                scheduleResult,
                submissionStatus,
                startedAt,
                endedAt
        );
        return scheduleJpaRepository.save(schedule);
    }

    private Application createApplication(String company, String position, NewStage newStage) {
        var newJobPosting = NewJobPosting.builder()
                .jobPostingUrl("jobPostingUrl")
                .company(company)
                .position(position)
                .jobLocation("seoul")
                .build();
        var newApplicationInfo = NewApplicationInfo.builder()
                .applicationMethod(ApplicationMethod.EMAIL)
                .deadline(now().plusDays(10))
                .build();

        return applicationManager.create(newJobPosting, newApplicationInfo, newStage, List.of(), member.getId());
    }

}