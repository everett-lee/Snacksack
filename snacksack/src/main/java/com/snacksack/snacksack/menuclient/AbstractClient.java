package com.snacksack.snacksack.menuclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.model.ApiMenuData;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.normaliser.Normaliser;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Set;

/**
 * Base class for the menuclient used to fetch menu data and return a Set of products.
 */
public abstract class AbstractClient<T extends ApiMenuData> {

    protected final ObjectMapper objectMapper;
    protected final String baseEndpoint;
    protected final HttpClient client;
    protected final Normaliser<T> normaliser;

    public AbstractClient(ObjectMapper objectMapper, HttpClient client, String baseEndpoint, Normaliser<T> normaliser) {
        this.objectMapper = objectMapper;
        this.baseEndpoint = baseEndpoint;
        this.client = client;
        this.normaliser = normaliser;
    }

    /**
     * @param uri The URI (including any query params) used to fetch the menu JSON data
     * @return A subclass of ApiMenuData representing the parsed response
     * @throws HttpClientErrorException
     */
    abstract T getMenuResponse(URI uri) throws HttpClientErrorException;

    /**
     * @param apiMenuData A subclass of ApiMenuData representing the parsed menu data
     * @return A Set of NormalisedProduct, representing the normalised representation of
     * products contained in the menu data.
     */
    abstract Set<NormalisedProduct> getProducts(T apiMenuData);
}

