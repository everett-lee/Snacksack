package com.snacksack.snacksack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.client.NandosClient;
import com.snacksack.snacksack.client.SpoonsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class AppConfig {

    static final ObjectMapper objectMapper = new ObjectMapper();
    static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Bean
    public ObjectMapper objectMapper() {
        return objectMapper;
    }
    @Bean
    public SpoonsClient spoonsClient(){
        return new SpoonsClient(objectMapper, httpClient);
    }

    @Bean
    public NandosClient nandosClient(){
        return new NandosClient(objectMapper, httpClient);
    }
}