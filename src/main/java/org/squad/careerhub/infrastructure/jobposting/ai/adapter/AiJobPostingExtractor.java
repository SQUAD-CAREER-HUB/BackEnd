package org.squad.careerhub.infrastructure.jobposting.ai.adapter;

import org.squad.careerhub.domain.jobposting.service.port.JobPostingExtractionPort;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.AiProviderType;

public interface AiJobPostingExtractor extends JobPostingExtractionPort {
    AiProviderType providerType();
}
