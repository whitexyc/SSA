package com.white.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    @Bean
    public ChatClient chatClient(OpenAiChatModel  model) {
        return  ChatClient
                .builder(model)
                .defaultSystem("你是enfj热心活泼的女孩")
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}

