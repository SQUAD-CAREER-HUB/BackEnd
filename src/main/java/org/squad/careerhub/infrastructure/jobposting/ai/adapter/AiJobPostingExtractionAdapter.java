package org.squad.careerhub.infrastructure.jobposting.ai.adapter;

import org.springframework.ai.chat.client.ChatClient;
import org.squad.careerhub.infrastructure.jobposting.ai.prompt.JobPostingPromptFactory;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.AiProviderType;

public class AiJobPostingExtractionAdapter
    extends AbstractAiJobPostingExtractionAdapter
    implements AiJobPostingExtractor {

    private final AiProviderType provider;

    public AiJobPostingExtractionAdapter(
        AiProviderType provider,
        ChatClient chatClient,
        JobPostingPromptFactory promptFactory
    ) {
        super(chatClient, promptFactory);
        this.provider = provider;
    }

    @Override
    public AiProviderType providerType() {
        return provider;
    }

    @Override
    protected String providerName() {
        return provider.name();
    }
}
