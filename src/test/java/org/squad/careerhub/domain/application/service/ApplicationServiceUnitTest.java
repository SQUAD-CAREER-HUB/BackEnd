package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.ApplicationFixture;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.application.service.dto.UpdateApplication;
import org.squad.careerhub.domain.member.entity.Member;

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
    void 지원서_생성_시_첨부파일과_전형단계와_일정을_생성하는_메서드를_호출한다() {
        // given
        var newApplicationDto =  mock(NewApplication.class);
        var application = mock(Application.class);
        var docsStage = mock(NewStage.class);
        given(application.getId()).willReturn(1L);

        given(applicationManager.create(any(), anyLong())).willReturn(application);

        // when
        Long applicationId = applicationService.createApplication(
                newApplicationDto,
                docsStage,
                List.of(),
                1L
        );

        // then
        assertThat(applicationId).isEqualTo(1L);
        verify(applicationFileManager, times(1)).addApplicationFile(any(), any());
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
        var application = ApplicationFixture.createApplicationInterview(mockMember);
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

}