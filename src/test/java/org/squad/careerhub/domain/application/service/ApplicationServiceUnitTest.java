package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.squad.careerhub.global.utils.DateTimeUtils.now;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.ScheduleResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.application.service.dto.UpdateApplication;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.service.dto.NewDocsSchedule;

@RequiredArgsConstructor
class ApplicationServiceUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationManager applicationManager;

    @Mock
    ApplicationPolicyValidator applicationPolicyValidator;

    @Mock
    ApplicationStageManager applicationStageManager;

    @Mock
    ApplicationFileManager applicationFileManager;

    @InjectMocks
    ApplicationService applicationService;

    @Test
    void 지원서_생성_시_전형단계와_일정을_생성하는_메서드를_호출한다() {
        // given
        var mockMember = mock(Member.class);
        var newApplicationDto = createNewApplication();
        var application = createApplication(mockMember, newApplicationDto);
        ReflectionTestUtils.setField(application, "id", 1L);

        doNothing().when(applicationPolicyValidator).validateNewStage(any(), any());
        given(applicationManager.create(any(), anyLong())).willReturn(application);

        // when
        Long applicationId = applicationService.createApplication(
                newApplicationDto,
                createDocumentNewStage(),
                List.of(),
                1L
        );

        // then
        assertThat(applicationId).isEqualTo(1L);
        verify(applicationStageManager, times(1)).createWithSchedule(any(), any());
    }

    @Test
    void 지원서_기본_정보와_첨부파일을_업데이트_한다() {
        // given
        var mockMember = mock(Member.class);
        var updateApplication = new UpdateApplication(
                1L,
                "https://www.careerhub.com/job/12345",
                "Naver",
                "BE",
                "Seoul, Korea",
                "memo"
        );
        var application = Application.create(
                mockMember,
                "https://www.careerhub.com/job/12345",
                "TechCorp",
                "Software Engineer",
                "New York, NY",
                StageType.INTERVIEW,
                ApplicationStatus.IN_PROGRESS,
                ApplicationMethod.EMAIL,
                now().plusDays(2)
        );
        application.update(
                updateApplication.jobPostingUrl(),
                updateApplication.company(),
                updateApplication.position(),
                updateApplication.jobLocation(),
                updateApplication.memo()
        );
        given(applicationManager.updateApplication(any(), any())).willReturn(application);

        // when
        applicationService.updateApplication(updateApplication, List.of(), 1L);

        // then
        verify(applicationManager, times(1)).updateApplication(any(), any());
        verify(applicationFileManager, times(1)).updateApplicationFile(any(), anyList());
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

    private NewStage createDocumentNewStage() {
        return NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .newDocsSchedule(new NewDocsSchedule(SubmissionStatus.NOT_SUBMITTED, ScheduleResult.WAITING))
                .newInterviewSchedules(List.of())
                .build();
    }

    private Application createApplication(Member mockMember, NewApplication newApplication) {
        return Application.create(
                mockMember,
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