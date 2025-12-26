package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.service.MemberReader;

class ApplicationManagerUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationFileManager applicationFileManager;

    @Mock
    ApplicationJpaRepository applicationJpaRepository;

    @Mock
    MemberReader memberReader;

    @InjectMocks
    ApplicationManager applicationManager;

    Member author;

    @BeforeEach
    void setUp() {
        author = Member.create("email@gmail.com", SocialProvider.KAKAO, "socialId", "nickname", "profileImageUrl");
    }

    @Test
    void 지원서가_생성될때_첨부파일_생성메서드가_호출된다() {
        // given
        var newJobPosting = createNewJobPosting();
        var newApplicationInfo = createNewApplicationInfo();
        var newStage = NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newInterviewSchedules(List.of())
                .build();
        var application = createApplication(newJobPosting, newStage, newApplicationInfo);

        given(memberReader.find(any())).willReturn(author);
        given(applicationJpaRepository.save(any())).willReturn(application);
        doNothing().when(applicationFileManager).addApplicationFile(any(), any());

        // when
        var savedApplication = applicationManager.create(newJobPosting, newApplicationInfo, newStage, List.of(), 1L);

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
                        newJobPosting.jobPostingUrl(),
                        newJobPosting.company(),
                        newJobPosting.position(),
                        newJobPosting.jobLocation(),
                        newStage.stageType(),
                        ApplicationStatus.IN_PROGRESS,
                        newApplicationInfo.applicationMethod(),
                        newApplicationInfo.deadline(),
                        null
                );

        // then
        verify(applicationFileManager, times(1)).addApplicationFile(any(), any());
    }

    private NewApplicationInfo createNewApplicationInfo() {
        return new NewApplicationInfo(
                ApplicationMethod.EMAIL,
                LocalDateTime.of(2020, 1, 1, 0, 0)
        );
    }

    private NewJobPosting createNewJobPosting() {
        return new NewJobPosting(
                "https://www.careerhub.com/job/12345",
                "TechCorp",
                "Software Engineer",
                "New York, NY"
        );
    }

    private Application createApplication(NewJobPosting newJobPosting, NewStage newStage, NewApplicationInfo newApplicationInfo) {
        return Application.create(
                author,
                newJobPosting.jobPostingUrl(),
                newJobPosting.company(),
                newJobPosting.position(),
                newJobPosting.jobLocation(),
                newStage.stageType(),
                newStage.finalApplicationStatus(),
                newApplicationInfo.applicationMethod(),
                newApplicationInfo.deadline()
        );
    }

}