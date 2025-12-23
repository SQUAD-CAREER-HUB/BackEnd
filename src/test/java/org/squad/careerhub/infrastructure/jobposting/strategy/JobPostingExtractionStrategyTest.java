package org.squad.careerhub.infrastructure.jobposting.strategy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContent;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.infrastructure.jobposting.ai.adapter.AiJobPostingExtractor;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.AiProviderType;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.JobPostingAiProperties;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.JobPostingExtractionStrategy;

class JobPostingExtractionStrategyTest extends TestDoubleSupport {

    @Mock AiJobPostingExtractor gemini;
    @Mock AiJobPostingExtractor solar;
    @Mock AiJobPostingExtractor qwen;
    @Mock AiJobPostingExtractor deepseek;
    @Mock AiJobPostingExtractor exaone;

    JobPostingAiProperties properties;
    JobPostingExtractionStrategy strategy;
    JobPostingContentReadResult readResult;

    @BeforeEach
    void setUp() {
        given(gemini.providerType()).willReturn(AiProviderType.GEMINI);
        given(solar.providerType()).willReturn(AiProviderType.SOLAR);
        given(qwen.providerType()).willReturn(AiProviderType.QWEN);
        given(deepseek.providerType()).willReturn(AiProviderType.DEEPSEEK);
        given(exaone.providerType()).willReturn(AiProviderType.EXAONE);

        properties = new JobPostingAiProperties();
        properties.setFallbackEnabled(true);
        properties.setPrimary(AiProviderType.DEEPSEEK);
        properties.setFallback(AiProviderType.GEMINI);

        strategy = new JobPostingExtractionStrategy(
            List.of(gemini, solar, qwen, deepseek, exaone),
            properties
        );

        // ✅ 생성자에서 providerType() 호출로 쌓인 interaction 제거
        clearInvocations(gemini, solar, qwen, deepseek, exaone);

        JobPostingContent content = JobPostingContent.builder()
            .url("https://example.com")
            .domain("example.com")
            .title("title")
            .bodyText("body")
            .build();
        readResult = JobPostingContentReadResult.success(content);
    }

    @Test
    void primary_DEEPSEEK_성공하면_fallback_호출안함() {
        JobPostingExtractResponse deepseekResponse = JobPostingExtractResponse.builder()
            .company("테스트회사")
            .status(JobPostingExtractStatus.SUCCESS)
            .build();

        given(deepseek.extractFromContent(readResult)).willReturn(deepseekResponse);

        JobPostingExtractResponse result = strategy.extractFromContent(readResult);

        assertThat(result).isEqualTo(deepseekResponse);
        then(deepseek).should(times(1)).extractFromContent(readResult);

        then(gemini).shouldHaveNoInteractions();
        then(solar).shouldHaveNoInteractions();
        then(qwen).shouldHaveNoInteractions();
        then(exaone).shouldHaveNoInteractions();
    }

    @Test
    void primary_GEMINI_quota_실패시_DEEPSEEK로_fallback() {
        // given
        properties.setPrimary(AiProviderType.GEMINI);
        properties.setFallback(AiProviderType.DEEPSEEK);

        CareerHubException quotaEx =
            new CareerHubException(ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED);

        given(gemini.extractFromContent(readResult)).willThrow(quotaEx);

        JobPostingExtractResponse deepseekResponse = JobPostingExtractResponse.builder()
            .company("대체회사")
            .status(JobPostingExtractStatus.SUCCESS)
            .build();
        given(deepseek.extractFromContent(readResult)).willReturn(deepseekResponse);

        // when
        JobPostingExtractResponse result = strategy.extractFromContent(readResult);

        // then
        assertThat(result).isEqualTo(deepseekResponse);

        then(gemini).should(times(1)).extractFromContent(readResult);
        then(deepseek).should(times(1)).extractFromContent(readResult);

        then(solar).shouldHaveNoInteractions();
        then(qwen).shouldHaveNoInteractions();
        then(exaone).shouldHaveNoInteractions();
    }

}
