package org.squad.careerhub.infrastructure.jobposting.ai.strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingContentReadResult;
import org.squad.careerhub.domain.jobposting.service.dto.response.JobPostingExtractResponse;
import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.infrastructure.jobposting.ai.adapter.AiJobPostingExtractor;

@Slf4j
@Component
@Qualifier("jobPostingExtractionStrategy")
@Primary
public class JobPostingExtractionStrategy implements JobPostingExtractionPort {

    private final Map<AiProviderType, AiJobPostingExtractor> extractorMap;
    private final JobPostingAiProperties properties;

    public JobPostingExtractionStrategy(
        List<AiJobPostingExtractor> extractors,
        JobPostingAiProperties properties
    ) {
        this.extractorMap = new EnumMap<>(AiProviderType.class);
        for (AiJobPostingExtractor e : extractors) {
            AiProviderType type = e.providerType();
            AiJobPostingExtractor prev = this.extractorMap.put(type, e);
            if (prev != null) {
                throw new IllegalStateException("Duplicate extractor for type=" + type);
            }
        }
        this.properties = properties;
    }

    @Override
    public JobPostingExtractResponse extractFromContent(JobPostingContentReadResult content) {
        AiJobPostingExtractor primary = getExtractor(properties.getPrimary());

        try {
            return primary.extractFromContent(content);
        } catch (CareerHubException e) {
            if (properties.isFallbackEnabled() && shouldFallback(e)) {
                return getExtractor(properties.getFallback()).extractFromContent(content);
            }
            throw e;
        }
    }

    private AiJobPostingExtractor getExtractor(AiProviderType type) {
        AiJobPostingExtractor port = extractorMap.get(type);
        if (port == null) throw new IllegalStateException("No extractor registered for type=" + type);
        return port;
    }

    private boolean shouldFallback(CareerHubException e) {
        return e.getErrorStatus() == ErrorStatus.JOB_POSTING_AI_QUOTA_EXCEEDED;
    }
}