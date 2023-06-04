package com.snacksack.snacksack.jedisclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class JedisClient {

    private final JedisPool jedisPool;

    private final ObjectMapper objectMapper;

    private final SetParams setParams;

    private final int YEAR_SECONDS = 60 * 60 * 24 * 365;

    public JedisClient(@Autowired JedisPool jedisPool, ObjectMapper objectMapper) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
        this.setParams = new SetParams();
        setParams.ex(YEAR_SECONDS);
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


        try {
            final String stringJson = objectMapper.writeValueAsString(products);
            log.info("Setting value for key: {}", key);

            try (Jedis jedis = jedisPool.getResource()){
                jedis.set(key, stringJson, this.setParams);
            }

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

        try (Jedis jedis = jedisPool.getResource()){
            String result = jedis.get(cacheKey);

            if (result == null) {
                log.info("Menu data for key: {} not present in cache", cacheKey);
                return Set.of();
            }

            try {
                final Set<NormalisedProduct> results = objectMapper.readValue(result, new TypeReference<>() {});
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
    }

    public Set<NormalisedProduct> getProducts(Restaurant restaurant) throws JsonProcessingException {
        // Item stored with id = 0 as default
        return getProducts(restaurant, 0);
    }

    public void setAnswer(Restaurant restaurant, int locationId, int money, Answer answer) throws JsonProcessingException {
        final String answerCacheKey = this.getAnswerCacheKey(restaurant, locationId, money);
        try {
            final String stringJson = objectMapper.writeValueAsString(answer);
            log.info("Setting value for key: {}", answerCacheKey);

            try (Jedis jedis = jedisPool.getResource()){
                jedis.set(answerCacheKey, stringJson, this.setParams);
            }

            log.info("Key: {} set", answerCacheKey);
        } catch (JsonProcessingException e) {
            log.error("Exception writing menu data to JSON string");
            throw e;
        } catch (Exception e) {
            log.error("Unhandled error {}", e.getMessage());
            throw e;
        }
    }

    public void setAnswer(Restaurant restaurant, int money, Answer answer) throws JsonProcessingException {
        // Store item with id = 0 where restaurant has no id
        this.setAnswer(restaurant, 0, money, answer);
    }

    public Answer getAnswer(Restaurant restaurant, int locationId, int money) throws JsonProcessingException {
        final String cacheKey = this.getAnswerCacheKey(restaurant, locationId, money);

        try (Jedis jedis = jedisPool.getResource()){
            final String result = jedis.get(cacheKey);

            if (result == null) {
                log.info("Answer for key: {} not present in cache", cacheKey);
                return new Answer(-1, List.of());
            }

            try {
                return objectMapper.readValue(result, Answer.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to process menu data json");
                throw e;
            } catch (Exception e) {
                log.error("Unhandled error {}", e.getMessage());
                throw e;
            }
        }
    }

    public Answer getAnswer(Restaurant restaurant, int money) throws JsonProcessingException {
        // Item stored with id = 0 as default
        return this.getAnswer(restaurant, 0, money);
    }

    private String getCacheKey(Restaurant restaurant, int locationId) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }

        return String.format("%s_%s", restaurant, locationId);
    }

    private String getAnswerCacheKey(Restaurant restaurant, int locationId, int money) {
        return getCacheKey(restaurant, locationId) + "_" + money + "_ANSWER";
    }

}

