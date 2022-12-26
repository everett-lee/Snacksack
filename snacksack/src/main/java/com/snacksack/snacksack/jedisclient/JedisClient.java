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
import redis.clients.jedis.params.SetParams;

import java.util.Base64;
import java.util.Set;

@Slf4j
@Service
public class JedisClient {

    private final Jedis jedis;
    private final ObjectMapper objectMapper;

    private final int YEAR_SECONDS = 60 * 60 * 24 * 365;

    public JedisClient(@Autowired Jedis jedis, ObjectMapper objectMapper) {
        this.jedis = jedis;
        this.objectMapper = objectMapper;
    }

    /**
     * Set the encoded JSON data for a given restaurant and restaurant id
     *
     * @param restaurant Enum representing the restaurant
     * @param locationId Id corresponding to restaurant location
     * @param products   A Set of products contained in the restaurant menu
     * @throws JsonProcessingException
     */
    public void setProducts(Restaurant restaurant, int locationId, Set<NormalisedProduct> products) throws JsonProcessingException {
        final String key = this.getCacheKey(restaurant, locationId);
        final SetParams setParams = new SetParams();
        setParams.ex(YEAR_SECONDS);

        try {
            final String stringJson = objectMapper.writeValueAsString(products);
            final String jsonStringEncoded = Base64.getEncoder().encodeToString(stringJson.getBytes());
            log.info("Setting value for key: {}", key);
            jedis.set(key, jsonStringEncoded, setParams);
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

    /**
     * Get products for a given restaurant and location ID
     *
     * @param restaurant Enum representing the restaurant
     * @param locationId Id corresponding to restaurant location
     * @return A Set of products contained in the restaurant menu
     * @throws JsonProcessingException
     */
    public Set<NormalisedProduct> getProducts(Restaurant restaurant, int locationId) throws JsonProcessingException {
        final String cacheKey = getCacheKey(restaurant, locationId);
        log.info("Getting stored menu data for key: {}", cacheKey);
        final String result = jedis.get(cacheKey);

        if (result == null) {
            log.info("Menu data for key: {} not present in cache", cacheKey);
            return Set.of();
        }

        try {
            final byte[] decodedBytes = Base64.getDecoder().decode(result);
            final String decodedJsonString = new String(decodedBytes);
            final Set<NormalisedProduct> results = objectMapper.readValue(decodedJsonString, new TypeReference<>() {});
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
        // Item stored with id = 0 as default
        return getProducts(restaurant, 0);
    }

    private String getCacheKey(Restaurant restaurant, int restaurantId) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }

        return String.format("%s_%s", restaurant, restaurantId);
    }

}

