package org.squad.careerhub.domain.application.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;

class ApplicationTest {

    Member author;

    @BeforeEach
    void setUp() {
        author = Member.create("email@gmail.com", SocialProvider.KAKAO, "socialId", "nickname", "profileImageUrl");
    }

    @Test
    void 지원서를_생성한다() {
        // when
        var application = Application.create(
                author,
                "http://jobposting.url",
                "CompanyName",
                "PositionName",
                "JobLocation",
                StageType.DOCUMENT,
                ApplicationStatus.FINAL_FAIL,
                ApplicationMethod.EMAIL,
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 10, 1)
        );
        // then
        assertThat(application).extracting(
                Application::getAuthor,
                Application::getJobPostingUrl,
                Application::getCompany,
                Application::getPosition,
                Application::getJobLocation,
                Application::getCurrentStageType,
                Application::getApplicationStatus,
                Application::getApplicationMethod,
                Application::getDeadline,
                Application::getSubmittedAt,
                Application::getMemo
        ).containsExactly(
                author,
                "http://jobposting.url",
                "CompanyName",
                "PositionName",
                "JobLocation",
                StageType.DOCUMENT,
                ApplicationStatus.IN_PROGRESS,
                ApplicationMethod.EMAIL,
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 10, 1),
                null
        );
    }

    @Test
    void 지원종료인_지원서를_생성한다() {
        // when
        var application = Application.create(
                author,
                "http://jobposting.url",
                "CompanyName",
                "PositionName",
                "JobLocation",
                StageType.APPLICATION_CLOSE,
                ApplicationStatus.FINAL_PASS,
                ApplicationMethod.EMAIL,
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 10, 1)
        );
        // then
        assertThat(application).extracting(
                Application::getAuthor,
                Application::getJobPostingUrl,
                Application::getCompany,
                Application::getPosition,
                Application::getJobLocation,
                Application::getCurrentStageType,
                Application::getApplicationStatus,
                Application::getApplicationMethod,
                Application::getDeadline,
                Application::getSubmittedAt,
                Application::getMemo
        ).containsExactly(
                author,
                "http://jobposting.url",
                "CompanyName",
                "PositionName",
                "JobLocation",
                StageType.APPLICATION_CLOSE,
                ApplicationStatus.FINAL_PASS,
                ApplicationMethod.EMAIL,
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 10, 1),
                null
        );
    }

}