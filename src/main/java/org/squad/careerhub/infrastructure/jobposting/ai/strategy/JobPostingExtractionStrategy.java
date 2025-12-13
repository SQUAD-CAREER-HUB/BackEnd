package org.squad.careerhub.infrastructure.jobposting.ai.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@Component
@Qualifier("jobPostingExtractionStrategy")
@Primary
public class JobPostingExtractionStrategy implements JobPostingExtractionPort {

    private final JobPostingExtractionPort geminiExtractor;
    private final JobPostingExtractionPort solarExtractor;
    private final JobPostingExtractionPort qwenExtractor;
    private final JobPostingAiProperties properties;

    public JobPostingExtractionStrategy(
        @Qualifier("geminiJobPostingExtractor") JobPostingExtractionPort geminiExtractor,
        @Qualifier("solarJobPostingExtractor") JobPostingExtractionPort solarExtractor,
        @Qualifier("qwenJobPostingExtractor") JobPostingExtractionPort qwenExtractor,
        JobPostingAiProperties properties
    ) {
        this.geminiExtractor = geminiExtractor;
        this.solarExtractor = solarExtractor;
        this.qwenExtractor = qwenExtractor;
        this.properties = properties;
    }

    @Override
    public JobPostingExtractResponse extractFromContent(JobPostingContentReadResult content) {
        JobPostingExtractionPort primary = getExtractor(properties.getPrimary());

        try {
            return primary.extractFromContent(content);
        } catch (CareerHubException e) {
            // 예: Gemini quota 초과 → SOLAR로 fallback
            if (properties.isFallbackEnabled() && shouldFallback(e)) {
                JobPostingExtractionPort fallback = getExtractor(properties.getFallback());
                return fallback.extractFromContent(content);
            }
            throw e;
        }
    }

    private JobPostingExtractionPort getExtractor(AiProviderType type) {
        return switch (type) {
            case QWEN -> qwenExtractor;
            case GEMINI -> geminiExtractor;
            case SOLAR -> solarExtractor;
        };
    }

    private boolean shouldFallback(CareerHubException e) {
        return e.getErrorStatus() == ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED;
    }
}
