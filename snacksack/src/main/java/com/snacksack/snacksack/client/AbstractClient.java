package com.snacksack.snacksack.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.PropertySource;

import java.net.http.HttpClient;

@PropertySource("classpath:application.properties")
public abstract class AbstractClient {
    final String BASE_ENDPOINT = "https://static.wsstack.nn4maws.net/content/v3/menus/{locationId}.json";
    final ObjectMapper objectMapper = new ObjectMapper();
    HttpClient client;

    public AbstractClient(HttpClient client) {
        this.client = client;
    }


}

