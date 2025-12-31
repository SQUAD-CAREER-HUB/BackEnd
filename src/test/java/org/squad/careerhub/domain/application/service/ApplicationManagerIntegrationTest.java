package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.UpdateApplication;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Transactional
class ApplicationManagerIntegrationTest extends IntegrationTestSupport {

    final ApplicationManager applicationManager;
    final ApplicationJpaRepository applicationJpaRepository;
    final MemberJpaRepository memberJpaRepository;
    final EntityManager entityManager;

    Member testMember;

    @BeforeEach
    void setUp() {
        testMember = memberJpaRepository.save(Member.create(
                "email",
                SocialProvider.KAKAO,
                "socialId",
                "nickname",
                "profileImageUrl")
        );
    }

    @Test
    void 지원서_기본_정보가_업데이트_된다() {
        // given
        var application = applicationJpaRepository.save(Application.create(
                testMember,
                "http://jobposting.url",
                "Test Company",
                "Software Engineer",
                "Remote",
                StageType.DOCUMENT,
                ApplicationStatus.IN_PROGRESS,
                ApplicationMethod.EMAIL,
                now()
        ));
        var updateApplication = new UpdateApplication(
                application.getId(),
                "http://newjobposting.url",
                "New Company",
                "Senior Software Engineer",
                "On-site",
                "Updated memo"
        );

        // when
        var updatedApplication = applicationManager.updateApplication(updateApplication, testMember.getId());
        entityManager.flush();

        // then
        assertThat(updatedApplication).isNotNull()
                .extracting(
                        Application::getJobPostingUrl,
                        Application::getCompany,
                        Application::getPosition,
                        Application::getJobLocation,
                        Application::getMemo
                ).containsExactly(
                        updateApplication.jobPostingUrl(),
                        updateApplication.company(),
                        updateApplication.position(),
                        updateApplication.jobLocation(),
                        updateApplication.memo()
                );
    }

    @Test
    void 본인의_지원서가_아니라면_수정할_수_없다() {
        // given
        var application = applicationJpaRepository.save(Application.create(
                testMember,
                "http://jobposting.url",
                "Test Company",
                "Software Engineer",
                "Remote",
                StageType.DOCUMENT,
                ApplicationStatus.IN_PROGRESS,
                ApplicationMethod.EMAIL,
                now()
        ));
        var updateApplication = new UpdateApplication(
                application.getId(),
                "http://newjobposting.url",
                "New Company",
                "Senior Software Engineer",
                "On-site",
                "Updated memo"
        );

        // when & then
        assertThatThrownBy(() -> applicationManager.updateApplication(updateApplication, 9999999L))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FORBIDDEN_MODIFY.getMessage());
    }

}