package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.domain.schedule.service.InterviewScheduleManager;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Transactional
class ApplicationServiceIntegrationTest extends IntegrationTestSupport {

    final ApplicationService applicationService;
    final ApplicationJpaRepository applicationRepository;
    final ApplicationStageJpaRepository applicationStageRepository;
    final MemberJpaRepository memberRepository;

    @MockitoBean
    InterviewScheduleManager interviewScheduleManager;

    @MockitoBean
    ApplicationFileManager applicationFileManager;

    Member testMember;
    MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(
                Member.create("test@example.com", SocialProvider.KAKAO, "socialId", "테스터", "imageUrl")
        );

        testFile = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    void 서류_전형_지원서가_정상_생성된다() {
        // given
        var jobPosting = createJobPosting("토스", "백엔드 개발자", "https://careers.com");
        var applicationInfo = createApplicationInfo();
        var documentStage = createDocumentNewStage();
        List<MultipartFile> files = List.of(testFile);

        doNothing().when(applicationFileManager).addApplicationFile(any(), any());

        // when
        Long applicationId = applicationService.createApplication(
                jobPosting,
                applicationInfo,
                documentStage,
                files,
                testMember.getId()
        );

        // then
        // Application 검증
        var application = applicationRepository.findById(applicationId).orElseThrow();

        assertThat(application).extracting(
                Application::getCompany,
                Application::getPosition,
                Application::getJobPostingUrl,
                Application::getApplicationMethod,
                Application::getCurrentStageType,
                Application::getDeadline,
                Application::getSubmittedAt,
                Application::getMemo
        ).containsExactly(
                jobPosting.company(),
                jobPosting.position(),
                jobPosting.jobPostingUrl(),
                applicationInfo.applicationMethod(),
                documentStage.stageType(),
                applicationInfo.deadline(),
                applicationInfo.submittedAt(),
                null
        );
        assertThat(application.getAuthor().getId()).isEqualTo(testMember.getId());

        // ApplicationStage 검증
        List<ApplicationStage> stages = applicationStageRepository.findByApplicationId(applicationId);

        assertThat(stages).hasSize(1);
        ApplicationStage stage = stages.getFirst();
        assertThat(stage).extracting(
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getStageStatus,
                ApplicationStage::getSubmissionStatus
        ).containsExactly(
                StageType.DOCUMENT,
                StageType.DOCUMENT.getDescription(),
                StageStatus.WAITING,
                SubmissionStatus.NOT_SUBMITTED
        );

        verify(applicationFileManager, times(1)).addApplicationFile(any(), any());
    }

    @Test
    void 서류_전형은_면접_일정_생성을_호출하지_않는다() {
        // given
        var documentStage = createDocumentNewStage();

        // when
        applicationService.createApplication(
                createJobPosting("쿠팡", "백엔드 개발자", "https://careers.coupang.com"),
                createApplicationInfo(),
                documentStage,
                List.of(),
                testMember.getId()
        );

        // then
        verify(interviewScheduleManager, never()).createInterviewSchedules();
        verify(interviewScheduleManager, never()).createEtcSchedules();
    }

    @Test
    void 면접_전형_지원서_생성_시_서류_PASS가_자동_생성된다() {
        // given
        var interviewNewStage = createInterviewNewStage();

        // when
        var jobPosting = createJobPosting("당근마켓", "안드로이드 개발자", "https://careers.daangn.com");
        var applicationInfo = createApplicationInfo();
        var applicationId = applicationService.createApplication(
                jobPosting,
                applicationInfo,
                interviewNewStage,
                List.of(),
                testMember.getId()
        );

        // then
        // Application 검증
        var application = applicationRepository.findById(applicationId).orElseThrow();
        assertThat(application).extracting(
                Application::getCompany,
                Application::getPosition,
                Application::getJobPostingUrl,
                Application::getApplicationMethod,
                Application::getCurrentStageType,
                Application::getDeadline,
                Application::getSubmittedAt,
                Application::getMemo
        ).containsExactly(
                jobPosting.company(),
                jobPosting.position(),
                jobPosting.jobPostingUrl(),
                applicationInfo.applicationMethod(),
                interviewNewStage.stageType(),
                applicationInfo.deadline(),
                applicationInfo.submittedAt(),
                null
        );

        List<ApplicationStage> stages = applicationStageRepository.findByApplicationId(applicationId);
        assertThat(stages).hasSize(2);

        // 서류 전형 검증
        var documentStage = stages.stream()
                .filter(s -> s.getStageType() == StageType.DOCUMENT)
                .findFirst()
                .orElseThrow();

        assertThat(documentStage).extracting(
                ApplicationStage::getSubmissionStatus,
                ApplicationStage::getStageName
        ).containsExactly(
                SubmissionStatus.SUBMITTED,
                StageType.DOCUMENT.getDescription()
        );

        // 면접 전형 검증
        var interviewStage = stages.stream()
                .filter(s -> s.getStageType() == StageType.INTERVIEW)
                .findFirst()
                .orElseThrow();

        assertThat(interviewStage).extracting(
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getStageStatus,
                ApplicationStage::getSubmissionStatus
        ).containsExactly(
                StageType.INTERVIEW,
                StageType.INTERVIEW.getDescription(),
                StageStatus.WAITING,
                null
        );
    }

    @Test
    void 면접_전형은_면접_일정_생성_Manager를_호출한다() {
        // given
        var interviewStage = createInterviewNewStage();

        // when
        applicationService.createApplication(
                createJobPosting("라인", "iOS 개발자", "https://careers.linecorp.com"),
                createApplicationInfo(),
                interviewStage,
                List.of(),
                testMember.getId()
        );

        // then
        verify(interviewScheduleManager).createInterviewSchedules();
        verify(interviewScheduleManager, never()).createEtcSchedules();
    }

    @Test
    void 기타_전형_지원서가_정상_생성된다() {
        // given
        var customStageName = "코딩테스트";
        var etcSchedule = new NewEtcSchedule(customStageName, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newEtcSchedule(etcSchedule)
                .newInterviewSchedules(List.of())
                .build();
        var jobPosting = createJobPosting("NHN", "게임 서버 개발자", "https://careers.nhn.com");
        var applicationInfo = createApplicationInfo();

        // when
        var applicationId = applicationService.createApplication(
                jobPosting,
                applicationInfo,
                etcNewStage,
                List.of(),
                testMember.getId()
        );

        // then
        // Application 검증
        var application = applicationRepository.findById(applicationId).orElseThrow();
        assertThat(application).extracting(
                Application::getCompany,
                Application::getPosition,
                Application::getJobPostingUrl,
                Application::getApplicationMethod,
                Application::getCurrentStageType,
                Application::getDeadline,
                Application::getSubmittedAt,
                Application::getMemo
        ).containsExactly(
                jobPosting.company(),
                jobPosting.position(),
                jobPosting.jobPostingUrl(),
                applicationInfo.applicationMethod(),
                etcNewStage.stageType(),
                applicationInfo.deadline(),
                applicationInfo.submittedAt(),
                null
        );

        List<ApplicationStage> stages = applicationStageRepository.findByApplicationId(applicationId);
        assertThat(stages).hasSize(2);
        // 기타 전형 검증
        var etcStage = stages.stream()
                .filter(s -> s.getStageType() == StageType.ETC)
                .findFirst()
                .orElseThrow();

        assertThat(etcStage).extracting(
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getStageStatus,
                ApplicationStage::getSubmissionStatus
        ).containsExactly(
                StageType.ETC,
                customStageName,
                StageStatus.WAITING,
                null
        );
    }

    @Test
    void 기타_전형_일정_미입력_시_기본_유형명_생성_테스트() {
        // given
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newInterviewSchedules(List.of())
                .build();

        // when
        var applicationId = applicationService.createApplication(
                createJobPosting("NHN", "게임 서버 개발자", "https://careers.nhn.com"),
                createApplicationInfo(),
                etcNewStage,
                List.of(),
                testMember.getId()
        );

        // then
        List<ApplicationStage> stages = applicationStageRepository.findByApplicationId(applicationId);
        assertThat(stages).hasSize(2);

        var etcStage = stages.stream()
                .filter(s -> s.getStageType() == StageType.ETC)
                .findFirst()
                .orElseThrow();

        assertThat(etcStage).extracting(
                ApplicationStage::getStageType,
                ApplicationStage::getStageName,
                ApplicationStage::getStageStatus,
                ApplicationStage::getSubmissionStatus
        ).containsExactly(
                StageType.ETC,
                StageType.ETC.getDescription(),
                StageStatus.WAITING,
                null
        );
    }

    @Test
    void 기타_전형은_기타_일정_생성_Manager를_호출한다() {
        // given
        var etcSchedule = new NewEtcSchedule("과제제출", LocalDateTime.now(), LocalDateTime.now().plusDays(5));
        var etcNewStage = NewStage.builder()
                .stageType(StageType.ETC)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .newEtcSchedule(etcSchedule)
                .newInterviewSchedules(List.of())
                .build();

        // when
        applicationService.createApplication(
                createJobPosting("SK텔레콤", "AI 엔지니어", "https://careers.sktelecom.com"),
                createApplicationInfo(),
                etcNewStage,
                List.of(),
                testMember.getId()
        );

        // then
        verify(interviewScheduleManager).createEtcSchedules();
        verify(interviewScheduleManager, never()).createInterviewSchedules();
    }

    @Test
    void 기타_전형도_서류_PASS가_자동_생성된다() {
        // given
        var etcSchedule = new NewEtcSchedule("인적성검사", LocalDateTime.now(), LocalDateTime.now().plusDays(4));
        var etcStage = NewStage.builder()
                .stageType(StageType.ETC)
                .newEtcSchedule(etcSchedule)
                .newInterviewSchedules(List.of())
                .build();

        // when
        var applicationId = applicationService.createApplication(
                createJobPosting("삼성전자", "하드웨어 엔지니어", "https://careers.samsung.com"),
                createApplicationInfo(),
                etcStage,
                List.of(),
                testMember.getId()
        );

        // then
        List<ApplicationStage> stages = applicationStageRepository.findByApplicationId(applicationId);
        assertThat(stages)
                .hasSize(2)
                .extracting(ApplicationStage::getStageType)
                .containsExactlyInAnyOrder(StageType.DOCUMENT, StageType.ETC);

        var documentStage = stages.stream()
                .filter(s -> s.getStageType() == StageType.DOCUMENT)
                .findFirst()
                .orElseThrow();

        assertThat(documentStage.getSubmissionStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
    }

    @Test
    void 지원종료_단계의_지원서가_정상_생성된다() {
        // given
        var closeNewStage = NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .newInterviewSchedules(List.of())
                .build();
        var jobPosting = createJobPosting("현대자동차", "자율주행 엔지니어", null);
        var applicationInfo = createApplicationInfo();

        // when
        var applicationId = applicationService.createApplication(
                jobPosting,
                applicationInfo,
                closeNewStage,
                List.of(),
                testMember.getId()
        );

        // then
        var application = applicationRepository.findById(applicationId).orElseThrow();
        // Application 검증
        assertThat(application).extracting(
                Application::getCompany,
                Application::getPosition,
                Application::getJobPostingUrl,
                Application::getApplicationMethod,
                Application::getCurrentStageType,
                Application::getDeadline,
                Application::getSubmittedAt,
                Application::getMemo
        ).containsExactly(
                jobPosting.company(),
                jobPosting.position(),
                jobPosting.jobPostingUrl(),
                applicationInfo.applicationMethod(),
                closeNewStage.stageType(),
                applicationInfo.deadline(),
                applicationInfo.submittedAt(),
                null
        );
        List<ApplicationStage> stages = applicationStageRepository.findByApplicationId(applicationId);
        assertThat(stages).isEmpty();
    }

    @Test
    void 존재하지_않는_회원_ID로_지원서_생성_시_예외가_발생한다() {
        // given
        var invalidMemberId = 999L;

        // when & then
        assertThatThrownBy(() ->
                applicationService.createApplication(
                        createJobPosting("네이버", "백엔드 개발자", null),
                        createApplicationInfo(),
                        createDocumentNewStage(),
                        List.of(),
                        invalidMemberId
                )
        ).isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_MEMBER.getMessage());
    }

    private NewJobPosting createJobPosting(String company, String position, String jobPostingUrl) {
        return NewJobPosting.builder()
                .company(company)
                .position(position)
                .jobPostingUrl(jobPostingUrl)
                .jobLocation("서울")
                .build();
    }

    private NewApplicationInfo createApplicationInfo() {
        return NewApplicationInfo.builder()
                .applicationMethod(ApplicationMethod.EMAIL)
                .deadline(LocalDate.now().plusDays(14))
                .submittedAt(LocalDate.now())
                .build();
    }

    private NewStage createDocumentNewStage() {
        return NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .newInterviewSchedules(List.of())
                .build();
    }

    private NewStage createInterviewNewStage() {
        return NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newInterviewSchedules(List.of())
                .build();
    }

}