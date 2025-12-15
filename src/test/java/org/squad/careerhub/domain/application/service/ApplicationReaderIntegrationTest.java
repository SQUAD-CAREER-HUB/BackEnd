package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;

@RequiredArgsConstructor
@Transactional
class ApplicationReaderIntegrationTest extends IntegrationTestSupport {

    final ApplicationReader applicationReader;
    final ApplicationJpaRepository applicationJpaRepository;
    final MemberJpaRepository memberJpaRepository;
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

}