package com.spring_ai.Rag_Knowledge_Assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient.Builder chatClientBuilder(
            @Qualifier("openAiChatModel") ChatModel chatModel) {

        return ChatClient.builder(chatModel);
    }
}