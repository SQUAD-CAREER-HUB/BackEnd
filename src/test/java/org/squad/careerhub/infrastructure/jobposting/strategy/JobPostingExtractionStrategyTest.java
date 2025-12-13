package org.squad.careerhub.infrastructure.jobposting.strategy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.AiProviderType;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.JobPostingAiProperties;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.JobPostingExtractionStrategy;

class JobPostingExtractionStrategyTest extends TestDoubleSupport {

    @Mock
    JobPostingExtractionPort geminiPort;

    @Mock
    JobPostingExtractionPort solarPort;

    @Mock
    JobPostingExtractionPort qwenPort;

    JobPostingAiProperties properties;
    JobPostingExtractionStrategy strategy;

    @BeforeEach
    void setUp() {
        properties = new JobPostingAiProperties();
        properties.setFallbackEnabled(true);
        properties.setPrimary(AiProviderType.QWEN);
        properties.setFallback(AiProviderType.GEMINI);

        strategy = new JobPostingExtractionStrategy(
            geminiPort, solarPort, qwenPort, properties
        );
    }

    @Test
    void primary_Qwen_성공하면_fallback_호출안함() {
        // given
        JobPostingContent content = JobPostingContent.builder()
            .url("https://example.com")
            .domain("example.com")
            .title("title")
            .bodyText("body")
            .build();
        JobPostingContentReadResult readResult =
            JobPostingContentReadResult.success(content);

        JobPostingExtractResponse qwenResponse = JobPostingExtractResponse.builder()
            .company("테스트회사")
            .status(JobPostingExtractStatus.SUCCESS)
            .build();

        given(qwenPort.extractFromContent(readResult)).willReturn(qwenResponse);

        // when
        JobPostingExtractResponse result = strategy.extractFromContent(readResult);

        // then
        assertThat(result).isEqualTo(qwenResponse);
        then(qwenPort).should(times(1)).extractFromContent(readResult);
        then(geminiPort).shouldHaveNoInteractions();
        then(solarPort).shouldHaveNoInteractions();
    }

    @Test
    void primary_실패시_GEMINI로_fallback() {
        // given
        JobPostingContent content = JobPostingContent.builder()
            .url("https://example.com")
            .domain("example.com")
            .title("title")
            .bodyText("body")
            .build();
        JobPostingContentReadResult readResult =
            JobPostingContentReadResult.success(content);

        CareerHubException quotaEx =
            new CareerHubException(ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED);

        given(qwenPort.extractFromContent(readResult)).willThrow(quotaEx);

        JobPostingExtractResponse geminiResponse = JobPostingExtractResponse.builder()
            .company("대체회사")
            .status(JobPostingExtractStatus.SUCCESS)
            .build();
        given(geminiPort.extractFromContent(readResult)).willReturn(geminiResponse);

        // when
        JobPostingExtractResponse result = strategy.extractFromContent(readResult);

        // then
        assertThat(result).isEqualTo(geminiResponse);
        then(qwenPort).should().extractFromContent(readResult);
        then(geminiPort).should().extractFromContent(readResult);
        then(solarPort).shouldHaveNoInteractions();
    }
}
