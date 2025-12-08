package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class ApplicationServiceUnitTest extends TestDoubleSupport {

    @Mock
    ApplicationManager applicationManager;

    @Mock
    ApplicationPolicyValidator applicationPolicyValidator;

    @InjectMocks
    ApplicationService applicationService;

    @Test
    void 지원서를_생성한다() {
        // given
        var newJobPosting = mock(NewJobPosting.class);
        var newApplicationInfo = mock(NewApplicationInfo.class);
        var authorId = 1L;

        // when
        applicationService.createApplication(newJobPosting, newApplicationInfo, authorId);

        // then
        verify(applicationPolicyValidator).validateMemoRule(newApplicationInfo);
        verify(applicationManager).create(newJobPosting, newApplicationInfo, authorId);
    }

    @Test
    void 메모_규칙_검증_실패시_예외를_던진다() {
        // given
        var newJobPosting = mock(NewJobPosting.class);
        var newApplicationInfo = mock(NewApplicationInfo.class);
        var authorId = 1L;

        doThrow(new CareerHubException(ErrorStatus.INVALID_MEMO_RULE))
                .when(applicationPolicyValidator).validateMemoRule(newApplicationInfo);

        // when & then
        assertThatThrownBy(() -> applicationService.createApplication(newJobPosting, newApplicationInfo, authorId))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_MEMO_RULE.getMessage());

        verify(applicationManager, never()).create(any(), any(), any());
    }

}