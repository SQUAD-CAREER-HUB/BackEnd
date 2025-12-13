package org.squad.careerhub.domain.application.controller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

class ApplicationCreateRequestTest {

    @Test
    void 서류_전형_지원서_생성_요청을_변환한다() {
        // given
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .jobPosting(createJobPostingRequest())
                .applicationInfo(createApplicationInfoRequest())
                .stage(createDocumentStageRequest())
                .build();

        // when
        NewJobPosting jobPosting = request.toNewJobPosting();
        NewApplicationInfo applicationInfo = request.toNewApplicationInfo();
        NewStage stage = request.toNewStage();

        // then
        assertThat(jobPosting).isNotNull();
        assertThat(applicationInfo).isNotNull();
        assertThat(stage).isNotNull();
        assertThat(stage.stageType()).isEqualTo(StageType.DOCUMENT);
    }

    @Test
    void 면접_전형_지원서_생성_요청을_변환한다() {
        // given
        var interviewSchedule = InterviewScheduleCreateRequest.builder()
                .name("1차 면접")
                .type(InterviewType.TECH)
                .scheduledAt(LocalDateTime.of(2025, 4, 1, 14, 0))
                .location("본사")
                .build();

        var stageRequest = StageRequest.builder()
                .stageType(StageType.INTERVIEW)
                .interviewSchedules(List.of(interviewSchedule))
                .build();

        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .jobPosting(createJobPostingRequest())
                .applicationInfo(createApplicationInfoRequest())
                .stage(stageRequest)
                .build();

        // when
        NewStage stage = request.toNewStage();

        // then
        assertThat(stage.stageType()).isEqualTo(StageType.INTERVIEW);
        assertThat(stage.newInterviewSchedules()).hasSize(1);
    }

    @Test
    void 기타_전형_지원서_생성_요청을_변환한다() {
        // given
        var etcSchedule = new EtcScheduleCreateRequest(
                "코딩 테스트",
                LocalDateTime.of(2025, 3, 28, 10, 0)
        );

        var stageRequest = StageRequest.builder()
                .stageType(StageType.ETC)
                .etcSchedule(etcSchedule)
                .build();

        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .jobPosting(createJobPostingRequest())
                .applicationInfo(createApplicationInfoRequest())
                .stage(stageRequest)
                .build();

        // when
        NewStage stage = request.toNewStage();

        // then
        assertThat(stage.stageType()).isEqualTo(StageType.ETC);
        assertThat(stage.newEtcSchedule()).isNotNull();
        assertThat(stage.newEtcSchedule().stageName()).isEqualTo("코딩 테스트");
    }

    @Test
    void toNewJobPosting으로_채용공고_정보를_변환한다() {
        // given
        var jobPostingRequest = JobPostingRequest.builder()
                .company("Naver")
                .position("Backend Developer")
                .jobLocation("Pangyo")
                .jobPostingUrl("https://careers.naver.com/123")
                .build();

        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .jobPosting(jobPostingRequest)
                .applicationInfo(createApplicationInfoRequest())
                .stage(createDocumentStageRequest())
                .build();

        // when
        NewJobPosting newJobPosting = request.toNewJobPosting();

        // then
        assertThat(newJobPosting.company()).isEqualTo("Naver");
        assertThat(newJobPosting.position()).isEqualTo("Backend Developer");
        assertThat(newJobPosting.jobLocation()).isEqualTo("Pangyo");
        assertThat(newJobPosting.jobPostingUrl()).isEqualTo("https://careers.naver.com/123");
    }

    @Test
    void toNewApplicationInfo로_지원_정보를_변환한다() {
        // given
        LocalDate deadline = LocalDate.of(2025, 4, 15);
        LocalDate submittedAt = LocalDate.of(2025, 4, 10);

        var applicationInfoRequest = ApplicationInfoRequest.builder()
                .deadline(deadline)
                .submittedAt(submittedAt)
                .applicationMethod(ApplicationMethod.HOMEPAGE)
                .build();

        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .jobPosting(createJobPostingRequest())
                .applicationInfo(applicationInfoRequest)
                .stage(createDocumentStageRequest())
                .build();

        // when
        NewApplicationInfo newApplicationInfo = request.toNewApplicationInfo();

        // then
        assertThat(newApplicationInfo.deadline()).isEqualTo(deadline);
        assertThat(newApplicationInfo.submittedAt()).isEqualTo(submittedAt);
        assertThat(newApplicationInfo.applicationMethod()).isEqualTo(ApplicationMethod.HOMEPAGE);
    }

    @Test
    void toNewStage로_전형_정보를_변환한다() {
        // given
        var stageRequest = StageRequest.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .build();

        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .jobPosting(createJobPostingRequest())
                .applicationInfo(createApplicationInfoRequest())
                .stage(stageRequest)
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.stageType()).isEqualTo(StageType.DOCUMENT);
        assertThat(newStage.submissionStatus()).isEqualTo(SubmissionStatus.NOT_SUBMITTED);
    }

    @Test
    void 모든_필드가_채워진_완전한_요청을_변환한다() {
        // given
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .jobPosting(JobPostingRequest.builder()
                        .company("Kakao")
                        .position("iOS Developer")
                        .jobLocation("Jeju")
                        .jobPostingUrl("https://careers.kakao.com/456")
                        .build())
                .applicationInfo(ApplicationInfoRequest.builder()
                        .deadline(LocalDate.of(2025, 5, 1))
                        .submittedAt(LocalDate.of(2025, 4, 28))
                        .applicationMethod(ApplicationMethod.EMAIL)
                        .build())
                .stage(StageRequest.builder()
                        .stageType(StageType.FINAL_PASS)
                        .build())
                .build();

        // when
        NewJobPosting jobPosting = request.toNewJobPosting();
        NewApplicationInfo applicationInfo = request.toNewApplicationInfo();
        NewStage stage = request.toNewStage();

        // then
        assertThat(jobPosting.company()).isEqualTo("Kakao");
        assertThat(applicationInfo.applicationMethod()).isEqualTo(ApplicationMethod.EMAIL);
        assertThat(stage.stageType()).isEqualTo(StageType.FINAL_PASS);
    }

    private JobPostingRequest createJobPostingRequest() {
        return JobPostingRequest.builder()
                .company("Test Company")
                .position("Software Engineer")
                .jobLocation("Seoul")
                .jobPostingUrl("https://example.com/job")
                .build();
    }

    private ApplicationInfoRequest createApplicationInfoRequest() {
        return ApplicationInfoRequest.builder()
                .deadline(LocalDate.now().plusDays(14))
                .submittedAt(LocalDate.now())
                .applicationMethod(ApplicationMethod.HOMEPAGE)
                .build();
    }

    private StageRequest createDocumentStageRequest() {
        return StageRequest.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .build();
    }

}