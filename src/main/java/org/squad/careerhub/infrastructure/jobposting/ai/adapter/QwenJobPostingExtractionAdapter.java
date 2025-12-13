package org.squad.careerhub.infrastructure.jobposting.ai.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.squad.careerhub.infrastructure.jobposting.ai.prompt.JobPostingPromptFactory;

@Slf4j
@Component
@Qualifier("qwenJobPostingExtractor")
public class QwenJobPostingExtractionAdapter extends  AbstractAiJobPostingExtractionAdapter{

    public QwenJobPostingExtractionAdapter(
        @Qualifier("qwenChatClient") ChatClient chatClient,
        JobPostingPromptFactory promptFactory
    ) {
        super(chatClient, promptFactory);
    }

    @Override
    protected String providerName() {
        return "QWEN";
    }
}
