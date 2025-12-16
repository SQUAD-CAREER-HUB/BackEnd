package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStage;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationStageJpaRepository;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Transactional
class ApplicationReaderIntegrationTest extends IntegrationTestSupport {

    final MemberJpaRepository memberJpaRepository;
    final ApplicationJpaRepository applicationJpaRepository;
    final ApplicationStageJpaRepository applicationStageJpaRepository;
    final ApplicationReader applicationReader;

    Member member;

    @BeforeEach
    void setUp() {
        member = memberJpaRepository.save(Member.create(
                "test@gmail.com",
                SocialProvider.KAKAO,
                "socialId",
                "TestUser",
                "profile.png"
        ));
    }

    @Test
    void 전체_지원서_개수와_각_전형별_지원서_개수를_조회한다() {
        // given
        var finalDocsApplications = createBulkApplications(member, StageType.DOCUMENT, 20);
        var etcApplications = createBulkApplications(member, StageType.ETC, 20);
        var interviewApplications = createBulkApplications(member, StageType.INTERVIEW, 20);
        var finalPassApplications = createBulkApplications(member, StageType.FINAL_PASS, 20);
        var finalFailApplications = createBulkApplications(member, StageType.FINAL_FAIL, 20);

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
                ApplicationStatisticsResponse::interviewStageCount,
                ApplicationStatisticsResponse::etcStageCount,
                ApplicationStatisticsResponse::finalPassedCount
        ).containsExactly(
                totalCount,
                interviewApplications.size(),
                etcApplications.size(),
                finalPassApplications.size()
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
        ).containsExactly(0,0,0,0);
    }

    @Test
    void 마감_되지_않은_서류_전형_지원서를_조회한다() {
        // given
        createBulkApplicationsWithStage(member, StageType.DOCUMENT, 10);
        createBulkApplicationsWithStage(member, StageType.ETC, 10);

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
                .allMatch(app -> !app.deadline().isBefore(LocalDate.now()));

        // 제출 상태 검증
        assertThat(response.contents()).hasSize(10)
                .extracting(BeforeDeadlineApplicationResponse::submissionStatus)
                .contains(SubmissionStatus.SUBMITTED, SubmissionStatus.NOT_SUBMITTED);
    }

    @Test
    void 커서_페이지네이선_작동을_확인한다() {
        // given
        createBulkApplicationsWithStage(member, StageType.DOCUMENT, 40);
        createBulkApplicationsWithStage(member, StageType.ETC, 10);

        // when
        var firstPage = applicationReader.findBeforeDeadlineApplications(member.getId(), new Cursor(null, 20));
        Long lastCursorId = firstPage.contents().getLast().applicationId();
        var secPage = applicationReader.findBeforeDeadlineApplications(member.getId(), new Cursor(lastCursorId, 20));

        // then
        // 첫번째 페이지
        assertThat(firstPage).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                true,
                lastCursorId
        );

        assertThat(firstPage.contents())
                .extracting(BeforeDeadlineApplicationResponse::submissionStatus)
                .contains(SubmissionStatus.SUBMITTED, SubmissionStatus.NOT_SUBMITTED);

        // 두번째 페이지
        assertThat(secPage).isNotNull().extracting(
                PageResponse::hasNext,
                PageResponse::nextCursorId
        ).containsExactly(
                false,
                null
        );

        assertThat(secPage.contents())
                .extracting(BeforeDeadlineApplicationResponse::submissionStatus)
                .contains(SubmissionStatus.SUBMITTED, SubmissionStatus.NOT_SUBMITTED);

    }

    private List<Application> createBulkApplications(Member member, StageType stageType, int count) {
        List<Application> applications = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Application app = Application.create(
                    member,
                    "https://example.com/job/" + i,
                    "회사" + i,
                    "포지션" + i,
                    "지역" + i,
                    stageType,
                    ApplicationMethod.values()[i % ApplicationMethod.values().length],
                    LocalDate.now().plusDays(30),
                    LocalDate.now()
            );
            applications.add(app);
        }

        return applicationJpaRepository.saveAll(applications);
    }

    private List<Application> createBulkApplicationsWithStage(Member member, StageType stageType, int count) {
        List<Application> applications = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Application app = Application.create(
                    member,
                    "https://example.com/job/" + i,
                    "회사" + i,
                    "포지션" + i,
                    "지역" + i,
                    stageType,
                    ApplicationMethod.values()[i % ApplicationMethod.values().length],
                    LocalDate.now().plusDays(30),
                    LocalDate.now()
            );

            applications.add(applicationJpaRepository.save(app));

            applicationStageJpaRepository.save(ApplicationStage.create(
                    app,
                    stageType,
                    stageType.getDescription(),
                    SubmissionStatus.values()[i % SubmissionStatus.values().length]
            ));
        }

        return applications;
    }

}