package com.snacksack.snacksack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.jedisclient.JedisClient;
import com.snacksack.snacksack.menuclient.NandosClient;
import com.snacksack.snacksack.menuclient.SpoonsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@Slf4j
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
    public SpoonsClient spoonsClient() {
        return new SpoonsClient(objectMapper, httpClient);
    }

    @Bean
    public NandosClient nandosClient() {
        return new NandosClient(objectMapper, httpClient);
    }

    @Bean
    public Jedis jedis() {
        final String host = "localhost";
        final int port = 6379;
        final Jedis jedis = new Jedis(host, port);
//        jedis.auth("password");
        jedis.get("abc");
        log.info("Connected to Redis on host: {} and port: {}", host, port);
        return jedis;
    }
}