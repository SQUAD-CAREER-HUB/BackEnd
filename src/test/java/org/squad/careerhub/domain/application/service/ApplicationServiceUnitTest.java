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

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.schedule.service.ScheduleManager;

@RequiredArgsConstructor
class ApplicationServiceUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationManager applicationManager;

    @Mock
    ApplicationPolicyValidator applicationPolicyValidator;

    @Mock
    ApplicationStageManager applicationStageManager;

    @InjectMocks
    ApplicationService applicationService;

    @Mock
    ScheduleManager scheduleManager;

    @Test
    void 지원서_생성_시_전형단계와_일정을_생성하는_메서드를_호출한다() {
        // given
        var mockMember = mock(Member.class);
        var application = Application.create(
                mockMember,
                "http://jobposting.url",
                "TestCompany",
                "TestPosition",
                "서울",
                StageType.DOCUMENT,
                null,
                ApplicationMethod.EMAIL,
                LocalDateTime.now().plusDays(14)
        );
        ReflectionTestUtils.setField(application, "id", 1L);

        doNothing().when(applicationPolicyValidator).validateNewStage(any());
        given(applicationManager.create(
                any(NewJobPosting.class),
                any(NewApplicationInfo.class),
                any(NewStage.class),
                anyList(),
                anyLong()
        )).willReturn(application);

        // when
        Long applicationId = applicationService.createApplication(
                createJobPosting("TestCompany", "TestPosition", "http://jobposting.url"),
                createApplicationInfo(),
                createDocumentNewStage(),
                List.of(),
                1L
        );

        // then
        assertThat(applicationId).isEqualTo(1L);
        verify(applicationStageManager, times(1)).createWithSchedule(any(), any());

    }


    private NewJobPosting createJobPosting(String company, String position, String jobPostingUrl) {
        return NewJobPosting.builder()
                .company(company)
                .position(position)
                .jobPostingUrl(jobPostingUrl)
                .jobLocation("서울")
                .build();
    }

    private NewApplicationInfo createApplicationInfo() {
        return NewApplicationInfo.builder()
                .applicationMethod(ApplicationMethod.EMAIL)
                .deadline(LocalDateTime.now().plusDays(14))
                .build();
    }

    private NewStage createDocumentNewStage() {
        return NewStage.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .newInterviewSchedules(List.of())
                .build();
    }

    private NewStage createInterviewNewStage() {
        return NewStage.builder()
                .stageType(StageType.INTERVIEW)
                .newInterviewSchedules(List.of())
                .build();
    }

}