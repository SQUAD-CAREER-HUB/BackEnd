package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
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
        var stageType = StageType.DOCUMENT;
        var newApplicationDto = NewApplication.builder()
                .jobPostingUrl("https://www.careerhub.com/job/12345")
                .company("네이버")
                .position("백엔드")
                .jobLocation("New York, NY")
                .deadline(now())
                .stageType(stageType)
                .applicationMethod(ApplicationMethod.EMAIL)
                .finalApplicationStatus(ApplicationStatus.IN_PROGRESS)
                .build();
        var app = applicationManager.create(newApplicationDto, member.getId());
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, stageType));
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf")
        );
        scheduleJpaRepository.save(Schedule.registerDocs(
                member,
                docsStage,
                stageType.getDescription(),
                SubmissionStatus.SUBMITTED,
                ScheduleResult.WAITING,
                now(),
                app.getDeadline()
        ));

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
        var newApplicationDto = NewApplication.builder()
                .jobPostingUrl("https://www.careerhub.com/job/12345")
                .company("네이버")
                .position("백엔드")
                .jobLocation("New York, NY")
                .deadline(now())
                .stageType(StageType.INTERVIEW)
                .applicationMethod(ApplicationMethod.EMAIL)
                .finalApplicationStatus(ApplicationStatus.IN_PROGRESS)
                .build();
        var app = applicationManager.create(newApplicationDto, member.getId());
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf"
        ));
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.DOCUMENT));
        var etcStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.ETC));
        var interviewStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.INTERVIEW));

        var docsSchedule = scheduleJpaRepository.save(Schedule.registerDocs(
                member,
                docsStage,
                StageType.DOCUMENT.getDescription(),
                SubmissionStatus.SUBMITTED,
                ScheduleResult.PASS,
                now(),
                app.getDeadline()
        ));
        var codingTestSchedule = scheduleJpaRepository.save(Schedule.registerEtc(
                member,
                etcStage,
                "코딩테스트",
                ScheduleResult.PASS,
                now(),
                now().plusDays(1)
        ));
        var studySchedule = scheduleJpaRepository.save(Schedule.registerEtc(
                member,
                etcStage,
                "과제전형",
                ScheduleResult.PASS,
                now().plusDays(2),
                now().plusDays(3)
        ));
        var firstInterview = scheduleJpaRepository.save(Schedule.registerInterview(
                member,
                interviewStage,
                "1차 면접",
                "판교",
                ScheduleResult.PASS,
                now().plusDays(3)
        ));
        var secInterview = scheduleJpaRepository.save(Schedule.registerInterview(
                member,
                interviewStage,
                "2차 면접",
                "판교",
                ScheduleResult.WAITING,
                now().plusDays(5)
        ));

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
                        codingTestSchedule.getEndedAt()
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
        var newApplicationDto = NewApplication.builder()
                .jobPostingUrl("https://www.careerhub.com/job/12345")
                .company("네이버")
                .position("백엔드")
                .jobLocation("New York, NY")
                .deadline(now())
                .stageType(StageType.ETC)
                .applicationMethod(ApplicationMethod.EMAIL)
                .finalApplicationStatus(ApplicationStatus.IN_PROGRESS)
                .build();
        var app = applicationManager.create(newApplicationDto, member.getId());
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf"
        ));
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.DOCUMENT));
        var etcStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.ETC));

        var docsSchedule = scheduleJpaRepository.save(Schedule.registerDocs(
                member,
                docsStage,
                StageType.DOCUMENT.getDescription(),
                SubmissionStatus.SUBMITTED,
                ScheduleResult.PASS,
                now(),
                app.getDeadline()
        ));

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
        var newApplicationDto = NewApplication.builder()
                .jobPostingUrl("https://www.careerhub.com/job/12345")
                .company("네이버")
                .position("백엔드")
                .jobLocation("New York, NY")
                .deadline(now())
                .stageType(StageType.APPLICATION_CLOSE)
                .applicationMethod(ApplicationMethod.EMAIL)
                .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
                .build();
        var app = applicationManager.create(newApplicationDto, member.getId());
        var attachment = applicationAttachmentJpaRepository.save(ApplicationAttachment.create(
                app,
                "fileUrl",
                "originalFileName",
                "pdf"
        ));
        var docsStage = applicationStageJpaRepository.save(ApplicationStage.create(app, StageType.DOCUMENT));


        var docsSchedule = scheduleJpaRepository.save(Schedule.registerDocs(
                member,
                docsStage,
                StageType.DOCUMENT.getDescription(),
                SubmissionStatus.SUBMITTED,
                ScheduleResult.PASS,
                now(),
                app.getDeadline()
        ));
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

}