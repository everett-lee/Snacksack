package com.snacksack.snacksack.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.model.ApiMenuData;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.normaliser.Normaliser;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;

public abstract class AbstractClient<T extends ApiMenuData> {
    final ObjectMapper objectMapper = new ObjectMapper();
    final String baseEndpoint;
    final HttpClient client;
    final Normaliser<T> normaliser;

    public AbstractClient(HttpClient client, String baseEndpoint, Normaliser<T> normaliser) {
        this.baseEndpoint = baseEndpoint;
        this.client = client;
        this.normaliser = normaliser;
    }

    abstract T getMenuResponse(URI uri) throws HttpClientErrorException;

    abstract List<NormalisedProduct> getProducts(T apiMenuData);
}

