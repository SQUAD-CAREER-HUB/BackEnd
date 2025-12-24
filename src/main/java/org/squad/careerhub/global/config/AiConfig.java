package org.squad.careerhub.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean("geminiChatClient")
    public ChatClient geminiChatClient(GoogleGenAiChatModel googleGenAiChatModel) {
        return ChatClient.builder(googleGenAiChatModel).build();
    }

    @Bean("solarChatClient")
    public ChatClient solarChatClient(OllamaApi ollamaApi) {
        OllamaChatOptions options = OllamaChatOptions.builder()
            .model("solar:latest")
            .build();

        OllamaChatModel model = OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(options)
            .build();

        return ChatClient.builder(model).build();
    }


    @Bean("qwenChatClient")
    public ChatClient qwenChatClient(OllamaApi ollamaApi) {
        OllamaChatOptions options = OllamaChatOptions.builder()
            .model("qwen2.5:7b-instruct")
            .build();

        OllamaChatModel model = OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(options)
            .build();

        return ChatClient.builder(model).build();
    }
    @Bean("deepseekChatClient")
    public ChatClient deepseekChatClient(OllamaApi ollamaApi) {
        OllamaChatOptions options = OllamaChatOptions.builder()
            .model("deepseek-r1:8b")
            .build();

        OllamaChatModel model = OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(options)
            .build();

        return ChatClient.builder(model).build();
    }

    @Bean("exaoneChatClient")
    public ChatClient exaoneChatClient(OllamaApi ollamaApi) {
        OllamaChatOptions options = OllamaChatOptions.builder()
            .model("exaone3.5:7.8b")
            .build();

        OllamaChatModel model = OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(options)
            .build();

        return ChatClient.builder(model).build();
    }
}
