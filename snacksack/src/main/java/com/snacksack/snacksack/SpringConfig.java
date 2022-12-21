package com.snacksack.snacksack;

import com.snacksack.snacksack.client.SpoonsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class SpringConfig {

    @Bean
    public SpoonsClient spoonsClient(){
        final HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        return new SpoonsClient(httpClient);
    }
}