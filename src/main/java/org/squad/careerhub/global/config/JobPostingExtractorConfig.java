package org.squad.careerhub.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.squad.careerhub.infrastructure.jobposting.ai.adapter.AiJobPostingExtractionAdapter;
import org.squad.careerhub.infrastructure.jobposting.ai.adapter.AiJobPostingExtractor;
import org.squad.careerhub.infrastructure.jobposting.ai.prompt.JobPostingPromptFactory;
import org.squad.careerhub.infrastructure.jobposting.ai.strategy.AiProviderType;

@Configuration
public class JobPostingExtractorConfig {

    @Bean("geminiJobPostingExtractor")
    AiJobPostingExtractor geminiExtractor(
        @Qualifier("geminiChatClient") ChatClient chatClient,
        JobPostingPromptFactory promptFactory
    ) {
        return new AiJobPostingExtractionAdapter(AiProviderType.GEMINI, chatClient, promptFactory);
    }

    @Bean("solarJobPostingExtractor")
    AiJobPostingExtractor solarExtractor(
        @Qualifier("solarChatClient") ChatClient chatClient,
        JobPostingPromptFactory promptFactory
    ) {
        return new AiJobPostingExtractionAdapter(AiProviderType.SOLAR, chatClient, promptFactory);
    }

    @Bean("qwenJobPostingExtractor")
    AiJobPostingExtractor qwenExtractor(
        @Qualifier("qwenChatClient") ChatClient chatClient,
        JobPostingPromptFactory promptFactory
    ) {
        return new AiJobPostingExtractionAdapter(AiProviderType.QWEN, chatClient, promptFactory);
    }

    @Bean("deepseekJobPostingExtractor")
    AiJobPostingExtractor deepseekExtractor(
        @Qualifier("deepseekChatClient") ChatClient chatClient,
        JobPostingPromptFactory promptFactory
    ) {
        return new AiJobPostingExtractionAdapter(AiProviderType.DEEPSEEK, chatClient, promptFactory);
    }

    @Bean("exaoneJobPostingExtractor")
    AiJobPostingExtractor exaoneExtractor(
        @Qualifier("exaoneChatClient") ChatClient chatClient,
        JobPostingPromptFactory promptFactory
    ) {
        return new AiJobPostingExtractionAdapter(AiProviderType.EXAONE, chatClient, promptFactory);
    }

}
