package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.squad.careerhub.domain.application.ApplicationFixture.createApplicationCloseStage;
import static org.squad.careerhub.domain.application.ApplicationFixture.createDocumentStage;
import static org.squad.careerhub.domain.application.ApplicationFixture.createEtcSchedule;
import static org.squad.careerhub.domain.application.ApplicationFixture.createEtcStageWithCustomSchedule;
import static org.squad.careerhub.domain.application.ApplicationFixture.createInterviewSchedule;
import static org.squad.careerhub.domain.application.ApplicationFixture.createInterviewStage;
import static org.squad.careerhub.domain.application.ApplicationFixture.createInterviewStageWithCustomSchedule;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.ApplicationFixture;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.domain.application.service.dto.response.DocsStage;
import org.squad.careerhub.domain.application.service.dto.response.ScheduleStage;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@Slf4j
@RequiredArgsConstructor
@Transactional
class ApplicationReaderIntegrationTest extends IntegrationTestSupport {

    final MemberJpaRepository memberJpaRepository;
    final ApplicationJpaRepository applicationJpaRepository;
    final ApplicationReader applicationReader;
    final ApplicationManager applicationManager;
    final MemberJpaRepository memberRepository;
    final ApplicationJpaRepository applicationRepository;
    final ApplicationStageManager applicationStageManager;

    Member member;

    @BeforeEach
    void setUp() {
        member = memberJpaRepository.save(MemberFixture.createMember());
    }

    @Test
    void 검색_조건_없이_전체_지원서를_조회한다() {
        // given
        int size = 10;
        createApplications(size);

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
    void 유형별_조회된_값들을_검증한다() {
        // given
        var docsApp = createApplication("FE", createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

        var baseTime = now();
        var newEtcSchedule = createEtcSchedule("코딩 테스트", baseTime.plusDays(1), baseTime.plusDays(1));
        var etcApp = createApplication("BE", createEtcStageWithCustomSchedule(newEtcSchedule));

        var newInterviewSchedule = createInterviewSchedule("임원 면접", baseTime.plusDays(1), "판교");
        var interviewApp = createApplication("DevOps", createInterviewStageWithCustomSchedule(newInterviewSchedule));

        var closeApp = createApplication("BE", createApplicationCloseStage());

        var condition = SearchCondition.builder().build();
        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(4);
        assertThat(response).extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                false,
                null
        );

        // 1. 서류 전형 검증
        var feResponse = response.contents().stream()
                .filter(r -> r.position().equals("FE"))
                .findFirst()
                .orElseThrow();

        assertThat(feResponse).isNotNull()
                .extracting(
                        ApplicationSummaryResponse::applicationId,
                        ApplicationSummaryResponse::company,
                        ApplicationSummaryResponse::position,
                        ApplicationSummaryResponse::currentStageType,
                        ApplicationSummaryResponse::currentScheduleResult,
                        ApplicationSummaryResponse::applicationStatus
                )
                .containsExactly(
                        docsApp.getId(),
                        docsApp.getCompany(),
                        docsApp.getPosition(),
                        StageType.DOCUMENT.getDescription(),
                        ScheduleResult.WAITING.name(),
                        ApplicationStatus.IN_PROGRESS.name()
                );
        assertThat(feResponse.scheduleStage()).isNull();
        assertThat(feResponse.docsStage()).isNotNull()
                .extracting(
                        DocsStage::deadline,
                        DocsStage::applicationMethod
                )
                .containsExactly(
                        docsApp.getDeadline().truncatedTo(ChronoUnit.MICROS),
                        docsApp.getApplicationMethod().getDescription()
                );

        // 2. 기타 전형 검증
        var beEtcResponse = response.contents().stream()
                .filter(r -> r.position().equals("BE") && r.currentStageType().equals(StageType.ETC.getDescription()))
                .findFirst()
                .orElseThrow();

        assertThat(beEtcResponse).isNotNull()
                .extracting(
                        ApplicationSummaryResponse::applicationId,
                        ApplicationSummaryResponse::company,
                        ApplicationSummaryResponse::position,
                        ApplicationSummaryResponse::currentStageType,
                        ApplicationSummaryResponse::currentScheduleResult,
                        ApplicationSummaryResponse::applicationStatus
                )
                .containsExactly(
                        etcApp.getId(),
                        etcApp.getCompany(),
                        etcApp.getPosition(),
                        StageType.ETC.getDescription(),
                        ScheduleResult.WAITING.name(),
                        ApplicationStatus.IN_PROGRESS.name()
                );

        assertThat(beEtcResponse.docsStage()).isNull();
        assertThat(beEtcResponse.scheduleStage()).isNotNull()
                .extracting(
                        ScheduleStage::scheduleName,
                        ScheduleStage::location,
                        ScheduleStage::nextScheduleAt
                )
                .containsExactly(
                        "코딩 테스트",
                        null,
                        newEtcSchedule.startedAt()
                );

        // 3. 면접 전형 검증 (DevOps)
        var devOpsResponse = response.contents().stream()
                .filter(r -> r.position().equals("DevOps"))
                .findFirst()
                .orElseThrow();

        assertThat(devOpsResponse).isNotNull()
                .extracting(
                        ApplicationSummaryResponse::applicationId,
                        ApplicationSummaryResponse::company,
                        ApplicationSummaryResponse::position,
                        ApplicationSummaryResponse::currentStageType,
                        ApplicationSummaryResponse::currentScheduleResult,
                        ApplicationSummaryResponse::applicationStatus
                )
                .containsExactly(
                        interviewApp.getId(),
                        interviewApp.getCompany(),
                        interviewApp.getPosition(),
                        StageType.INTERVIEW.getDescription(),
                        ScheduleResult.WAITING.name(),
                        ApplicationStatus.IN_PROGRESS.name()
                );

        assertThat(devOpsResponse.docsStage()).isNull();
        assertThat(devOpsResponse.scheduleStage()).isNotNull()
                .extracting(
                        ScheduleStage::scheduleName,
                        ScheduleStage::location,
                        ScheduleStage::nextScheduleAt
                )
                .containsExactly(
                        newInterviewSchedule.scheduleName(),
                        newInterviewSchedule.location(),
                        newInterviewSchedule.startedAt()
                );

        // 4. 지원 마감 검증
        var closedResponse = response.contents().stream()
                .filter(r -> r.position().equals("BE") && r.currentStageType()
                        .equals(StageType.APPLICATION_CLOSE.getDescription()))
                .findFirst()
                .orElseThrow();

        assertThat(closedResponse)
                .extracting(
                        ApplicationSummaryResponse::applicationId,
                        ApplicationSummaryResponse::company,
                        ApplicationSummaryResponse::position,
                        ApplicationSummaryResponse::currentStageType,
                        ApplicationSummaryResponse::currentScheduleResult,
                        ApplicationSummaryResponse::applicationStatus
                )
                .containsExactly(
                        closeApp.getId(),
                        closeApp.getCompany(),
                        closeApp.getPosition(),
                        StageType.APPLICATION_CLOSE.getDescription(),
                        null,
                        ApplicationStatus.FINAL_PASS.name()
                );

        // 지원 마감은 schedule이 없을 수 있음
        assertThat(closedResponse.docsStage()).isNull();
        assertThat(closedResponse.scheduleStage()).isNull();
    }

    @Test
    void 회사명으로_검색하여_지원서를_조회한다() {
        // given
        createApplication("카카오", "백엔드 개발자", createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

        var baseTime = now();
        var newEtcSchedule = createEtcSchedule("코딩 테스트", baseTime, baseTime.plusHours(1));
        var etcApp = createApplication("네이버", "백엔드 개발자", createEtcStageWithCustomSchedule(newEtcSchedule));

        createApplication("라인", "백엔드 개발자", createInterviewStage(baseTime.plusDays(1), "판교"));

        var condition = SearchCondition.builder()
                .query("네이버")
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        var docsSummaryResponse = response.contents().getFirst();
        assertThat(docsSummaryResponse).extracting(
                ApplicationSummaryResponse::applicationId,
                ApplicationSummaryResponse::company,
                ApplicationSummaryResponse::position,
                ApplicationSummaryResponse::currentStageType,
                ApplicationSummaryResponse::currentScheduleResult
        ).containsExactly(
                etcApp.getId(),
                etcApp.getCompany(),
                etcApp.getPosition(),
                etcApp.getCurrentStageType().getDescription(),
                ScheduleResult.WAITING.name()
        );
        assertThat(docsSummaryResponse.docsStage()).isNull();
        assertThat(docsSummaryResponse.scheduleStage()).extracting(
                ScheduleStage::scheduleName,
                ScheduleStage::location,
                ScheduleStage::nextScheduleAt
        ).containsExactly(
                newEtcSchedule.scheduleName(),
                null,
                newEtcSchedule.startedAt()
        );
    }

    @Test
    void 직무명으로_검색하여_지원서를_조회한다() {
        // given
        createApplication("카카오", "FE 개발자", createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

        var baseTime = now();
        var newEtcSchedule = createEtcSchedule("코딩 테스트", baseTime, baseTime.plusHours(1));
        createApplication("네이버", "QA", createEtcStageWithCustomSchedule(newEtcSchedule));

        var newInterviewSchedule = createInterviewSchedule("기술 면접", baseTime.plusDays(1), "판교");
        var interviewApp = createApplication("라인", "백엔드 개발자", createInterviewStageWithCustomSchedule(newInterviewSchedule));
        var condition = SearchCondition.builder()
                .query("백엔드")
                .build();
        var cursor = Cursor.of(null, 10);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        var interviewSummaryResponse = response.contents().getFirst();
        assertThat(interviewSummaryResponse).extracting(
                ApplicationSummaryResponse::applicationId,
                ApplicationSummaryResponse::company,
                ApplicationSummaryResponse::position,
                ApplicationSummaryResponse::currentStageType,
                ApplicationSummaryResponse::currentScheduleResult
        ).containsExactly(
                interviewApp.getId(),
                interviewApp.getCompany(),
                interviewApp.getPosition(),
                interviewApp.getCurrentStageType().getDescription(),
                ScheduleResult.WAITING.name()
        );
        assertThat(interviewSummaryResponse.docsStage()).isNull();
        assertThat(interviewSummaryResponse.scheduleStage()).extracting(
                ScheduleStage::scheduleName,
                ScheduleStage::location,
                ScheduleStage::nextScheduleAt
        ).containsExactly(
                newInterviewSchedule.scheduleName(),
                newInterviewSchedule.location(),
                newInterviewSchedule.startedAt()
        );
    }

    @Test
    void 전형_타입으로_필터링하여_지원서를_조회한다() {
        // given
        createApplication("카카오", "FE 개발자", createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

        var baseTime = now();
        var newEtcSchedule = createEtcSchedule("코딩 테스트", baseTime, baseTime.plusHours(1));
        createApplication("네이버", "QA", createEtcStageWithCustomSchedule(newEtcSchedule));

        var newInterviewSchedule = createInterviewSchedule("기술 면접", baseTime.plusDays(1), "판교");
        var interviewApp = createApplication("라인", "백엔드 개발자", createInterviewStageWithCustomSchedule(newInterviewSchedule));

        var condition = SearchCondition.builder()
                .stageTypes(List.of(StageType.INTERVIEW))
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        var interviewSummaryResponse = response.contents().getFirst();
        assertThat(interviewSummaryResponse).extracting(
                ApplicationSummaryResponse::applicationId,
                ApplicationSummaryResponse::company,
                ApplicationSummaryResponse::position,
                ApplicationSummaryResponse::currentStageType,
                ApplicationSummaryResponse::currentScheduleResult
        ).containsExactly(
                interviewApp.getId(),
                interviewApp.getCompany(),
                interviewApp.getPosition(),
                interviewApp.getCurrentStageType().getDescription(),
                ScheduleResult.WAITING.name()
        );
        assertThat(interviewSummaryResponse.docsStage()).isNull();
        assertThat(interviewSummaryResponse.scheduleStage()).extracting(
                ScheduleStage::scheduleName,
                ScheduleStage::location,
                ScheduleStage::nextScheduleAt
        ).containsExactly(
                newInterviewSchedule.scheduleName(),
                newInterviewSchedule.location(),
                newInterviewSchedule.startedAt()
        );
    }

    @Test
    void 여러_가지_전형_타입으로_필터링하여_지원서를_조회한다() {
        // given
        var docsApp = createApplication("카카오", "FE 개발자", createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

        var baseTime = now();
        var newEtcSchedule = createEtcSchedule("코딩 테스트", baseTime, baseTime.plusHours(1));
        createApplication("네이버", "QA", createEtcStageWithCustomSchedule(newEtcSchedule));

        var newInterviewSchedule = createInterviewSchedule("기술 면접", baseTime.plusDays(1), "판교");
        var interviewApp = createApplication("라인", "백엔드 개발자", createInterviewStageWithCustomSchedule(newInterviewSchedule));

        var condition = SearchCondition.builder()
                .stageTypes(List.of(StageType.INTERVIEW, StageType.DOCUMENT))
                .build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(2)
                .extracting(ApplicationSummaryResponse::company)
                .containsExactlyInAnyOrder(docsApp.getCompany(), interviewApp.getCompany());
    }

    @Test
    void 서류_제출_상태로_필터링하여_지원서를_조회한다() {
        // given
        var appSubmitted = createApplication("카카오", "FE 개발자",
                createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

        createApplication("네이버", "FE 개발자", createInterviewStage(now(), "강남"));

        var condition = new SearchCondition(
                null,
                List.of(StageType.DOCUMENT),
                List.of(SubmissionStatus.SUBMITTED),
                null
        );
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response =
                applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);

        var res = response.contents().getFirst();
        assertThat(res).extracting(
                ApplicationSummaryResponse::applicationId,
                ApplicationSummaryResponse::company,
                ApplicationSummaryResponse::position,
                ApplicationSummaryResponse::currentStageType,
                ApplicationSummaryResponse::currentScheduleResult
        ).containsExactly(
                appSubmitted.getId(),
                appSubmitted.getCompany(),
                appSubmitted.getPosition(),
                appSubmitted.getCurrentStageType().getDescription(),
                ScheduleResult.WAITING.name()
        );

        assertThat(res.scheduleStage()).isNull();
        assertThat(res.docsStage()).isNotNull()
                .extracting(DocsStage::deadline, DocsStage::applicationMethod)
                .containsExactly(
                        appSubmitted.getDeadline(),
                        appSubmitted.getApplicationMethod().getDescription()
                );
    }

    @Test
    void 가장_가까운_면접_일정이_포함되어_조회된다() {
        // given
        var baseTime = now();
        var newInterviewSchedule = createInterviewSchedule("기술 면접", baseTime.plusDays(1), "판교");
        var interviewApp = createApplication("카카오", "백엔드 개발자", createInterviewStageWithCustomSchedule(newInterviewSchedule));

        var condition = SearchCondition.builder().build();
        var cursor = Cursor.of(null, 20);

        // when
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor, member.getId());

        // then
        assertThat(response.contents()).hasSize(1);
        var interviewSummaryResponse = response.contents().getFirst();
        assertThat(interviewSummaryResponse).extracting(
                ApplicationSummaryResponse::applicationId,
                ApplicationSummaryResponse::company,
                ApplicationSummaryResponse::position,
                ApplicationSummaryResponse::currentStageType,
                ApplicationSummaryResponse::currentScheduleResult
        ).containsExactly(
                interviewApp.getId(),
                interviewApp.getCompany(),
                interviewApp.getPosition(),
                interviewApp.getCurrentStageType().getDescription(),
                ScheduleResult.WAITING.name()
        );
        assertThat(interviewSummaryResponse.docsStage()).isNull();
        var responseScheduleStage = interviewSummaryResponse.scheduleStage();
        assertThat(responseScheduleStage).isNotNull()
                .extracting(
                        ScheduleStage::scheduleName,
                        ScheduleStage::location,
                        ScheduleStage::nextScheduleAt
                )
                .containsExactly(
                        newInterviewSchedule.scheduleName(),
                        newInterviewSchedule.location(),
                        newInterviewSchedule.startedAt()
                );
    }

    @Test
    void 커서_기반_페이지네이션이_정상_작동한다() {
        // given
        createApplications(15);
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
        createApplication("백엔드 개발자", createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

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
        PageResponse<ApplicationSummaryResponse> response = applicationReader.findApplications(condition, cursor,
                otherMember.getId());

        // then
        assertThat(response).isNotNull().extracting(
                PageResponse::contents,
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                List.of(),
                false,
                null
        );
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
        createApplication("카카오", "백엔드 개발자", createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING));

        var baseTime = now();
        var newEtcSchedule = createEtcSchedule("코딩 테스트", baseTime, baseTime.plusHours(1));
        createApplication("네이버", "QA 개발자", createEtcStageWithCustomSchedule(newEtcSchedule));

        createApplication("라인", "백엔드 개발자", createInterviewStage(baseTime.plusDays(1), "판교"));

        var condition = new SearchCondition(
                "개발자",
                List.of(StageType.INTERVIEW, StageType.ETC),
                List.of(),
                List.of()
        );
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
    void 전체_지원서_개수와_각_전형별_지원서_개수를_조회한다() {
        // given
        var finalDocsApplications = createBulkApplications(member, StageType.DOCUMENT, ApplicationStatus.FINAL_PASS, 20);
        var etcApplications = createBulkApplications(member, StageType.ETC, ApplicationStatus.FINAL_PASS, 20);
        var interviewApplications = createBulkApplications(member, StageType.INTERVIEW, ApplicationStatus.FINAL_PASS, 20);
        var finalPassApplications = createBulkApplications(member, StageType.APPLICATION_CLOSE, ApplicationStatus.FINAL_PASS, 20);
        var finalFailApplications = createBulkApplications(member, StageType.APPLICATION_CLOSE, ApplicationStatus.FINAL_FAIL, 20);

        int totalCount = finalDocsApplications.size()
                + etcApplications.size()
                + interviewApplications.size()
                + finalPassApplications.size()
                + finalFailApplications.size();
        // when
        var applicationStatistics = applicationReader.getApplicationStatistics(member.getId());

        // then
        assertThat(applicationStatistics).extracting(
                ApplicationStatisticsResponse::totalApplicationCount,
                ApplicationStatisticsResponse::docStageCount,
                ApplicationStatisticsResponse::interviewStageCount,
                ApplicationStatisticsResponse::etcStageCount,
                ApplicationStatisticsResponse::finalPassedCount,
                ApplicationStatisticsResponse::finalFailedCount
        ).containsExactly(
                totalCount,
                finalDocsApplications.size(),
                interviewApplications.size(),
                etcApplications.size(),
                finalPassApplications.size(),
                finalFailApplications.size()
        );
    }

    @Test
    void 지원서가_존재하지않으면_0개를_반환한다() {
        // when
        var applicationStatistics = applicationReader.getApplicationStatistics(member.getId());

        // then
        assertThat(applicationStatistics).extracting(
                ApplicationStatisticsResponse::totalApplicationCount,
                ApplicationStatisticsResponse::interviewStageCount,
                ApplicationStatisticsResponse::etcStageCount,
                ApplicationStatisticsResponse::finalPassedCount
        ).containsExactly(0, 0, 0, 0);
    }

    @Test
    void 마감_되지_않은_서류_전형_지원서를_조회한다() {
        // given
        var documentStage = createDocumentStage(SubmissionStatus.SUBMITTED, ScheduleResult.WAITING);
        var notSubmittedDocsStage = createDocumentStage(SubmissionStatus.NOT_SUBMITTED, ScheduleResult.WAITING);
        createApplication("company", "BE", documentStage, now().minusDays(1));
        createApplication("company", "BE", notSubmittedDocsStage, now().plusDays(1));
        createApplication("company", "BE", documentStage, now().plusDays(2));

        // when
        var response = applicationReader.findBeforeDeadlineApplications(member.getId(), new Cursor(null, 20));

        // then
        assertThat(response).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                false,
                null
        );

        // 모든 지원서의 데드라인이 미래인지 검증
        assertThat(response.contents())
                .allMatch(app -> !app.deadline().isBefore(now()));

        // 제출 상태 검증
        assertThat(response.contents()).hasSize(2)
                .extracting(BeforeDeadlineApplicationResponse::submissionStatus)
                .contains(SubmissionStatus.SUBMITTED, SubmissionStatus.NOT_SUBMITTED);
    }

    private List<Application> createBulkApplications(
            Member member,
            StageType stageType,
            ApplicationStatus applicationStatus,
            int count
    ) {
        List<Application> applications = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            var application = ApplicationFixture.createApplication(member, stageType, applicationStatus);
            applications.add(application);
        }

        if (stageType == StageType.APPLICATION_CLOSE) {
            applications.forEach(app -> app.updateApplicationStatus(applicationStatus));
        }

        return applicationJpaRepository.saveAll(applications);
    }

    private void createApplications(int size) {
        for (int i = 0; i < size; i++) {
            applicationRepository.save(createApplication(
                    "Position " + i,
                    createDocumentStage(SubmissionStatus.NOT_SUBMITTED, ScheduleResult.WAITING)
            ));
        }
    }

    private Application createApplication(String position, NewStage newStage) {
        return createApplication("company", position, newStage, now().plusDays(10));
    }

    private Application createApplication(String company, String position, NewStage newStage) {
        return createApplication(company, position, newStage, now().plusDays(10));
    }

    private Application createApplication(String company, String position, NewStage newStage, LocalDateTime deadline) {
        ApplicationStatus applicationStatus = newStage.stageType() == StageType.APPLICATION_CLOSE ?
                ApplicationStatus.FINAL_PASS : ApplicationStatus.IN_PROGRESS;

        NewApplication newApplication = ApplicationFixture.createNewApplication(
                company,
                position,
                newStage.stageType(),
                applicationStatus,
                deadline
        );

        Application app = applicationManager.create(newApplication, member.getId());
        applicationStageManager.createWithSchedule(app, newStage);

        return app;
    }

}