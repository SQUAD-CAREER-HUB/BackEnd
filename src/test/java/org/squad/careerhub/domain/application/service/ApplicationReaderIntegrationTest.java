package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageResult;
import org.squad.careerhub.domain.application.entity.StageStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.domain.schedule.entity.InterviewSchedule;
import org.squad.careerhub.domain.schedule.enums.InterviewType;
import org.squad.careerhub.domain.schedule.repository.InterviewScheduleJpaRepository;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Transactional
class ApplicationReaderIntegrationTest extends IntegrationTestSupport {

    final ApplicationReader applicationReader;
    final MemberJpaRepository memberRepository;
    final ApplicationJpaRepository applicationRepository;
    final ApplicationStageJpaRepository applicationStageRepository;
    final InterviewScheduleJpaRepository interviewScheduleRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.create(
                "test@example.com",
                SocialProvider.KAKAO,
                "socialId123",
                "테스터",
                "profileImageUrl"
        );
        memberRepository.save(member);
    }

    @Test
    void 검색_조건_없이_전체_지원서를_조회한다() {
        // given
        int size = 10;
        createApplicationsForPaginationTest(member, size);

        var condition = SearchCondition.builder().build();
        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(size);
        assertThat(response).extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                false,
                null
        );
    }

    @Test
    void 회사명으로_검색하여_지원서를_조회한다() {
        // given
        createApplicationWithStage(member, "카카오", "백엔드 개발자", StageType.DOCUMENT);
        createApplicationWithStage(member, "네이버", "프론트엔드 개발자", StageType.INTERVIEW);
        createApplicationWithStage(member, "라인", "DevOps 엔지니어", StageType.ETC);

        var condition = SearchCondition.builder()
                .query("카카오")
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        assertThat(response.contents().getFirst()).extracting(
                ApplicationSummaryResponse::company,
                ApplicationSummaryResponse::position,
                ApplicationSummaryResponse::currentStageType,
                ApplicationSummaryResponse::currentStageStatus
        ).containsExactly(
                "카카오",
                "백엔드 개발자",
                StageType.DOCUMENT.getDescription(),
                StageStatus.WAITING.name()
        );
    }

    @Test
    void 직무명으로_검색하여_지원서를_조회한다() {
        // given
        createApplicationWithStage(member, "카카오", "백엔드 개발자", StageType.DOCUMENT);
        createApplicationWithStage(member, "네이버", "프론트엔드 개발자", StageType.INTERVIEW);
        createApplicationWithStage(member, "라인", "DevOps 엔지니어", StageType.ETC);

        var condition = SearchCondition.builder()
                .query("백엔드")
                .build();
        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        assertThat(response.contents().getFirst()).extracting(
                ApplicationSummaryResponse::company,
                ApplicationSummaryResponse::position,
                ApplicationSummaryResponse::currentStageType,
                ApplicationSummaryResponse::currentStageStatus
        ).containsExactly(
                "카카오",
                "백엔드 개발자",
                StageType.DOCUMENT.getDescription(),
                StageStatus.WAITING.name()
        );
    }

    @Test
    void 전형_타입으로_필터링하여_지원서를_조회한다() {
        // given
        createApplicationWithStage(member, "카카오", "백엔드 개발자", StageType.INTERVIEW);
        createApplicationWithStage(member, "네이버", "프론트엔드 개발자", StageType.DOCUMENT);
        createApplicationWithStage(member, "쿠팡", "DevOps 개발자", StageType.ETC);

        var condition = SearchCondition.builder()
                .stageTypes(List.of(StageType.INTERVIEW))
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        assertThat(response.contents().getFirst())
                .extracting(
                        ApplicationSummaryResponse::company,
                        ApplicationSummaryResponse::position,
                        ApplicationSummaryResponse::currentStageType,
                        ApplicationSummaryResponse::currentStageStatus
                ).containsExactly(
                        "카카오",
                        "백엔드 개발자",
                        StageType.INTERVIEW.getDescription(),
                        StageStatus.WAITING.name()
                );
    }

    @Test
    void 여러_가지_전형_타입으로_필터링하여_지원서를_조회한다() {
        // given
        var app1 = createApplicationWithStage(member, "카카오", "백엔드 개발자", StageType.INTERVIEW);
        createApplicationStage(app1, StageType.DOCUMENT, SubmissionStatus.SUBMITTED);

        createApplicationWithStage(member, "네이버", "프론트엔드 개발자", StageType.DOCUMENT);

        var condition = SearchCondition.builder()
                .stageTypes(List.of(StageType.INTERVIEW, StageType.DOCUMENT))
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(2)
                .extracting(ApplicationSummaryResponse::company)
                .containsExactlyInAnyOrder("네이버", "카카오");
    }

    @Test
    void 서류_제출_상태로_필터링하여_지원서를_조회한다() {
        // given
        var app1 = createApplicationWithStage(member, "카카오", "백엔드 개발자", StageType.DOCUMENT);
        createApplicationStage(app1, StageType.DOCUMENT, SubmissionStatus.SUBMITTED);

        var app2 = createApplicationWithStage(member, "네이버", "프론트엔드 개발자", StageType.DOCUMENT);
        createApplicationStage(app2, StageType.DOCUMENT, SubmissionStatus.NOT_SUBMITTED);

        var condition = SearchCondition.builder()
                .stageTypes(List.of(StageType.DOCUMENT))
                .submissionStatus(List.of(SubmissionStatus.SUBMITTED))
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        assertThat(response.contents().getFirst().company()).isEqualTo("카카오");
    }

    @Test
    void 가장_가까운_면접_일정이_포함되어_조회된다() {
        // given
        var kakaoApp = createApplicationWithStage(member, "카카오", "백엔드 개발자", StageType.INTERVIEW);
        createApplicationSchedule(
                kakaoApp,
                "1차 면접",
                InterviewType.EXECUTIVE,
                "판교",
                LocalDateTime.of(2025, 12, 20, 10, 0)
        );
        createApplicationSchedule(
                kakaoApp,
                "2차 면접",
                InterviewType.EXECUTIVE,
                "판교",
                LocalDateTime.of(2025, 12, 25, 14, 0)
        );

        var naverApp = createApplicationWithStage(member, "네이버", "프론트엔드 개발자", StageType.DOCUMENT);

        var condition = SearchCondition.builder().build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response =
                applicationReader.findApplications(condition, cursor, member.getId());

        // then
        var kakaoResponse = response.contents().stream()
                .filter(app -> app.company().equals("카카오"))
                .findFirst()
                .orElseThrow();

        assertThat(kakaoResponse.nextInterviewDate())
                .isNotNull()
                .isEqualTo(LocalDateTime.of(2025, 12, 20, 10, 0));

        var naverResponse = response.contents().stream()
                .filter(app -> app.company().equals("네이버"))
                .findFirst()
                .orElseThrow();

        assertThat(naverResponse.nextInterviewDate()).isNull();
    }

    @Test
    void 커서_기반_페이지네이션이_정상_작동한다() {
        // given
        createRandomApplications(member, 15);
        var condition = SearchCondition.builder().build();
        var firstCursor = Cursor.of(null, 10);

        // when - 첫 페이지
        var firstPage = applicationReader.findApplications(condition, firstCursor, member.getId());

        // then - 첫 페이지
        assertThat(firstPage.contents()).hasSize(10);
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(firstPage.nextCursorId()).isNotNull();

        // when - 두 번째 페이지
        var secondCursor = Cursor.of(firstPage.nextCursorId(), 10);
        var secondPage = applicationReader.findApplications(condition, secondCursor, member.getId());

        // then - 두 번째 페이지
        assertThat(secondPage.contents()).hasSize(5);
        assertThat(secondPage.hasNext()).isFalse();
        assertThat(secondPage.nextCursorId()).isNull();
    }

    @Test
    void 다른_사용자의_지원서는_조회되지_않는다() {
        // given
        createApplicationWithStage(member, "카카오", "백엔드", StageType.DOCUMENT);

        var otherMember = Member.create(
                "other@example.com",
                SocialProvider.GOOGLE,
                "otherSocialId",
                "다른사용자",
                "otherProfileUrl"
        );
        memberRepository.save(otherMember);

        var condition = SearchCondition.builder().build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response =
                applicationReader.findApplications(condition, cursor, otherMember.getId());

        // then
        assertThat(response.contents()).isEmpty();
        assertThat(response.hasNext()).isFalse();
    }

    @Test
    void 빈_결과에서도_페이지네이션_정보가_올바르게_반환된다() {
        // given
        var emptyMember = Member.create(
                "empty@example.com",
                SocialProvider.KAKAO,
                "emptySocialId",
                "지원서없는사용자",
                "emptyProfileUrl"
        );
        memberRepository.save(emptyMember);

        var condition = SearchCondition.builder().build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor,
                emptyMember.getId());

        // then
        assertThat(response.contents()).isEmpty();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.nextCursorId()).isNull();
    }

    @Test
    void 복합_조건으로_검색이_가능하다() {
        // given
        var app1 = createApplicationWithStage(member, "카카오", "백엔드 개발자", StageType.INTERVIEW);
        createPassedDocumentStage(app1);
        var app2 = createApplicationWithStage(member, "쿠팡", "백엔드 개발자", StageType.ETC);
        createPassedDocumentStage(app2);

        createApplicationWithStage(member, "네이버", "프론트엔드 개발자", StageType.DOCUMENT);
        createApplicationWithStage(member, "라인", "백엔드 개발자", StageType.DOCUMENT);

        var condition = SearchCondition.builder()
                .query("개발자")
                .stageTypes(List.of(StageType.INTERVIEW, StageType.ETC))
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(2);
        assertThat(response.contents()).extracting(
                ApplicationSummaryResponse::company
        ).containsExactlyInAnyOrder(
                "카카오",
                "쿠팡"
        );
    }

    @Test
    void 전형_합격_필터링으로_검색이_가능하다() {
        // given
        var app1 = createApplication(member, "네이버", "프론트엔드 개발자", StageType.DOCUMENT);
        var applicationStage1 = createApplicationStage(app1, StageType.DOCUMENT, SubmissionStatus.SUBMITTED);
        applicationStage1.updateStageStatus(StageStatus.PASS);

        var app2 = createApplication(member, "라인", "백엔드 개발자", StageType.INTERVIEW);
        var applicationStage2 = createApplicationStage(app2, StageType.INTERVIEW, null);
        applicationStage2.updateStageStatus(StageStatus.PASS);

        var app3 = createApplication(member, "쿠팡", "백엔드 개발자", StageType.INTERVIEW);
        var applicationStage3 = createApplicationStage(app3, StageType.INTERVIEW, null);

        var condition = SearchCondition.builder()
                .stageTypes(List.of(StageType.INTERVIEW, StageType.DOCUMENT))
                .stageResult(List.of(StageResult.STAGE_PASS))
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(2);
        assertThat(response.contents()).extracting(
                ApplicationSummaryResponse::company
        ).containsExactlyInAnyOrder(
                "네이버",
                "라인"
        );
    }

    @Test
    void 랜덤_StageType_으로_다양한_지원서를_생성하고_조회한다() {
        // given
        int size = 20;
        createRandomApplications(member, size);

        var condition = SearchCondition.builder().build();
        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(cursor.limit());
        assertThat(response.hasNext()).isTrue();
        assertThat(response.nextCursorId()).isNotNull();
    }

    private Application createApplicationWithStage(
            Member member,
            String company,
            String position,
            StageType stageType
    ) {
        Application app = Application.create(
                member,
                "jobPosting",
                company,
                position,
                "서울",
                stageType,
                ApplicationMethod.EMAIL,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        applicationRepository.save(app);

        SubmissionStatus submissionStatus = stageType == StageType.DOCUMENT
                ? SubmissionStatus.NOT_SUBMITTED
                : null;

        createApplicationStage(app, stageType, submissionStatus);

        return app;
    }

    private Application createApplication(
            Member member,
            String company,
            String position,
            StageType stageType
    ) {
        Application app = Application.create(
                member,
                "jobPosting",
                company,
                position,
                "서울",
                stageType,
                ApplicationMethod.EMAIL,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        applicationRepository.save(app);

        return app;
    }

    private void createApplicationsForPaginationTest(Member member, int size) {
        for (int i = 1; i <= size; i++) {
            Application app = Application.create(
                    member,
                    "jobPosting" + i,
                    "회사" + i,
                    "포지션" + i,
                    "지역" + i,
                    StageType.DOCUMENT,
                    ApplicationMethod.EMAIL,
                    LocalDate.now(),
                    LocalDate.now().plusDays(30)
            );
            applicationRepository.save(app);
            createApplicationStage(app, StageType.DOCUMENT, SubmissionStatus.NOT_SUBMITTED);
        }
    }

    private void createRandomApplications(Member member, int size) {
        StageType[] stageTypes = StageType.values();
        SubmissionStatus[] submissionStatuses = SubmissionStatus.values();

        for (int i = 1; i <= size; i++) {
            StageType randomStageType = stageTypes[ThreadLocalRandom.current().nextInt(stageTypes.length)];
            Application app = Application.create(
                    member,
                    "jobPosting" + i,
                    "회사" + i,
                    "포지션" + i,
                    "지역" + i,
                    randomStageType,
                    ApplicationMethod.EMAIL,
                    LocalDate.now(),
                    LocalDate.now().plusDays(30)
            );
            applicationRepository.save(app);

            SubmissionStatus submissionStatus = randomStageType == StageType.DOCUMENT
                    ? submissionStatuses[ThreadLocalRandom.current().nextInt(submissionStatuses.length)]
                    : null;

            createApplicationStage(app, randomStageType, submissionStatus);

            // 면접 전형이면 랜덤 면접 일정 추가
            if (randomStageType == StageType.INTERVIEW) {
                int scheduleCount = ThreadLocalRandom.current().nextInt(1, 4);
                for (int j = 0; j < scheduleCount; j++) {
                    LocalDateTime scheduledAt = LocalDateTime.now()
                            .plusDays(ThreadLocalRandom.current().nextInt(1, 31))
                            .withHour(ThreadLocalRandom.current().nextInt(9, 18))
                            .withMinute(0);

                    createApplicationSchedule(
                            app,
                            (j + 1) + "차 면접",
                            InterviewType.EXECUTIVE,
                            "서울",
                            scheduledAt
                    );
                }
            }
        }
    }

    private ApplicationStage createApplicationStage(
            Application app,
            StageType stageType,
            SubmissionStatus submissionStatus
    ) {
        ApplicationStage stage = ApplicationStage.create(
                app,
                stageType,
                stageType.getDescription(),
                submissionStatus
        );
        return applicationStageRepository.save(stage);
    }

    private void createPassedDocumentStage(Application app) {
        ApplicationStage stage = ApplicationStage.createPassedDocumentStage(app);
        applicationStageRepository.save(stage);
    }

    private void createApplicationSchedule(
            Application app,
            String title,
            InterviewType type,
            String location,
            LocalDateTime dateTime
    ) {
        InterviewSchedule schedule = InterviewSchedule.register(
                app,
                title,
                type,
                location,
                dateTime
        );
        interviewScheduleRepository.save(schedule);
    }

}