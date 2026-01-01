package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.service.MemberReader;

class ApplicationManagerUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationJpaRepository applicationJpaRepository;

    @Mock
    MemberReader memberReader;

    @InjectMocks
    ApplicationManager applicationManager;

    Member author;

    @BeforeEach
    void setUp() {
        author = MemberFixture.createMember();
    }

    @Test
    void 지원서가_생성될때_첨부파일_생성메서드가_호출된다() {
        // given
        var newApplicationDto = createNewApplication();
        var application = createApplication(newApplicationDto);

        given(memberReader.find(any())).willReturn(author);
        given(applicationJpaRepository.save(any())).willReturn(application);

        // when
        var savedApplication = applicationManager.create(newApplicationDto, 1L);

        assertThat(savedApplication).isNotNull()
                .extracting(
                        Application::getAuthor,
                        Application::getJobPostingUrl,
                        Application::getCompany,
                        Application::getPosition,
                        Application::getJobLocation,
                        Application::getCurrentStageType,
                        Application::getApplicationStatus,
                        Application::getApplicationMethod,
                        Application::getDeadline,
                        Application::getMemo
                ).containsExactly(
                        author,
                        newApplicationDto.jobPostingUrl(),
                        newApplicationDto.company(),
                        newApplicationDto.position(),
                        newApplicationDto.jobLocation(),
                        newApplicationDto.stageType(),
                        ApplicationStatus.IN_PROGRESS,
                        newApplicationDto.applicationMethod(),
                        newApplicationDto.deadline(),
                        null
                );
    }

    private NewApplication createNewApplication() {
        return NewApplication.builder()
                .jobPostingUrl("https://www.careerhub.com/job/12345")
                .company("TechCorp")
                .position("Software Engineer")
                .jobLocation("New York, NY")
                .deadline(LocalDateTime.of(2020, 1, 1, 0, 0))
                .stageType(StageType.INTERVIEW)
                .applicationMethod(ApplicationMethod.EMAIL)
                .finalApplicationStatus(ApplicationStatus.IN_PROGRESS)
                .build();
    }

    private Application createApplication(NewApplication newApplication) {
        return Application.create(
                author,
                newApplication.jobPostingUrl(),
                newApplication.company(),
                newApplication.position(),
                newApplication.jobLocation(),
                newApplication.stageType(),
                newApplication.finalApplicationStatus(),
                newApplication.applicationMethod(),
                newApplication.deadline()
        );
    }

}