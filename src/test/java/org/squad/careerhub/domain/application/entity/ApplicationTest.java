package org.squad.careerhub.domain.application.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.application.ApplicationFixture;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.entity.Member;

class ApplicationTest {

    Member author;

    @BeforeEach
    void setUp() {
        author = MemberFixture.createMember();
    }

    @Test
    void 지원서를_생성한다() {
        // when
        var docsApp = ApplicationFixture.createApplicationDocs(author);
        var etcApp = ApplicationFixture.createApplicationEtc(author);
        var interviewApp = ApplicationFixture.createApplicationInterview(author);
        var closedApp = ApplicationFixture.createApplicationClosed(author, ApplicationStatus.FINAL_PASS);

        // then
        assertThat(docsApp).extracting(
                Application::getAuthor,
                Application::getJobPostingUrl,
                Application::getCompany,
                Application::getPosition,
                Application::getJobLocation,
                Application::getCurrentStageType,
                Application::getApplicationStatus,
                Application::getApplicationMethod
        ).containsExactly(
                author,
                "http://jobposting.url",
                "CompanyName",
                "PositionName",
                "JobLocation",
                StageType.DOCUMENT,
                ApplicationStatus.IN_PROGRESS,
                ApplicationMethod.EMAIL
        );

        assertThat(docsApp.getDeadline()).isNotNull();
        assertThat(docsApp.getMemo()).isNull();
        assertThat(etcApp.getCurrentStageType()).isEqualTo(StageType.ETC);
        assertThat(interviewApp.getCurrentStageType()).isEqualTo(StageType.INTERVIEW);
        assertThat(closedApp.getCurrentStageType()).isEqualTo(StageType.APPLICATION_CLOSE);
    }

}