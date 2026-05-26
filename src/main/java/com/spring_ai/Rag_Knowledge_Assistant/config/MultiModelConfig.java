package com.spring_ai.Rag_Knowledge_Assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MultiModelConfig {
    @Value("${gemini.api.key}")
    private String geminiKey;
    @Value("${gemini.api.url}")
    private String gmeiniUrl;
    @Value("${gemini.api.completions.path}")
    private String completionPath;
    @Value("${gemini.api.model.name}")
    private String geminiModelName;
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder().maxMessages(10).build();
    }


    @Bean("openaiChatClient")
    @Primary
    public ChatClient openAIChatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory){
        ChatClient.Builder builder =ChatClient.builder(openAiChatModel);

        builder.defaultAdvisors(
                new SimpleLoggerAdvisor(),
                MessageChatMemoryAdvisor.builder(chatMemory).build()
        );// log request and response for debugging
        ChatClient client = builder.build();
        return client;
    }

    @Bean("geminiChatClient")
    public ChatClient geminiChatClient( ChatMemory chatMemory) {
        OpenAiApi geminiApi = OpenAiApi.builder()
                .baseUrl(gmeiniUrl)
                .completionsPath(completionPath)
                .apiKey(geminiKey)
                .build();

        OpenAiChatModel geminiModel = OpenAiChatModel.builder()
                .openAiApi(geminiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(geminiModelName)
                        .temperature(1.0)
                        .build())
                .build();

        return ChatClient.builder(geminiModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                ) // log request and response for debugging
                .build();
    }
}
