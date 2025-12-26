package org.squad.careerhub.domain.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
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
    ApplicationStageManager applicationStageManager;

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
    void 지원_종료_단계_지원서가_생성될_경우_전형단계가_생성되지_않고_지원서만_생성된다() {
        // given
         var newJobPosting = createNewJobPosting();
        var newApplicationInfo = createNewApplicationInfo();
        var newStage = NewStage.builder()
                .stageType(StageType.APPLICATION_CLOSE)
                .finalApplicationStatus(ApplicationStatus.FINAL_PASS)
                .newInterviewSchedules(List.of())
                .build();

        var application = createApplication(newJobPosting, newStage, newApplicationInfo);

        given(memberReader.find(any())).willReturn(author);
        given(applicationJpaRepository.save(any())).willReturn(application);

        // when
        applicationManager.createWithStage(newJobPosting, newApplicationInfo, newStage, 1L);

        // then
        verify(applicationStageManager, never()).create(any(), any());
    }

    @Test
    void 진행중인_지원서가_생성될_경우_전형단계도_함께_생성한다() {
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

        // when
        applicationManager.createWithStage(newJobPosting, newApplicationInfo, newStage, 1L);

        // then
        verify(applicationStageManager, times(1)).create(any(), any());
    }

    private NewApplicationInfo createNewApplicationInfo() {
        return new NewApplicationInfo(
                ApplicationMethod.EMAIL,
                LocalDate.of(2020, 1, 1)
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