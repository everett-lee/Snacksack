package com.snacksack.snacksack.jedisclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Base64;
import java.util.Set;

@Slf4j
@Service
public class JedisClient {

    private final Jedis jedis;
    private final ObjectMapper objectMapper;

    public JedisClient(@Autowired Jedis jedis, ObjectMapper objectMapper) {
        this.jedis = jedis;
        this.objectMapper = objectMapper;
    }

    public void setProducts(Restaurant restaurant, int restaurantId, Set<NormalisedProduct> products) throws JsonProcessingException {
        final String key = getCacheKey(restaurant, restaurantId);

        try {
            final String stringJson = objectMapper.writeValueAsString(products);
            final String jsonStringEncoded = Base64.getEncoder().encodeToString(stringJson.getBytes());
            log.info("Setting value for key: {}", key);
            jedis.set(key, jsonStringEncoded);
            log.info("Key: {} set", key);
        } catch (JsonProcessingException e) {
            log.error("Exception writing menu data to JSON string");
            throw e;
        } catch (Exception e) {
            log.error("Unhandled error {}", e.getMessage());
            throw e;
        }
    }

    public void setProducts(Restaurant restaurant, Set<NormalisedProduct> products) throws JsonProcessingException {
        // Store item with id = 0 where restaurant has no id
        this.setProducts(restaurant, 0, products);
    }

    public Set<NormalisedProduct> getProducts(Restaurant restaurant, int restaurantId) throws JsonProcessingException {
        final String cacheKey = getCacheKey(restaurant, restaurantId);
        log.info("Getting stored menu data for key: {}", cacheKey);
        final String result = jedis.get(cacheKey);

        if (result == null) {
            log.info("Menu data for key: {} not present in cache", cacheKey);
            return Set.of();
        }

        try {
            final byte[] decodedBytes = Base64.getDecoder().decode(result);
            final String decodedJsonString = new String(decodedBytes);
            Set<NormalisedProduct> results = objectMapper.readValue(decodedJsonString, new TypeReference<>() {
            });
            log.info("Returning {} results", results.size());
            return results;
        } catch (JsonProcessingException e) {
            log.error("Failed to process menu data json");
            throw e;
        } catch (Exception e) {
            log.error("Unhandled error {}", e.getMessage());
            throw e;
        }
    }

    public Set<NormalisedProduct> getProducts(Restaurant restaurant) throws JsonProcessingException {
        return getProducts(restaurant, 0);
    }

    private String getCacheKey(Restaurant restaurant, int restaurantId) {
        return String.format("%s_%s", restaurant, restaurantId);
    }

}

