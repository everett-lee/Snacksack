package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.snacksack.snacksack.menuclient.SpoonsClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import com.snacksack.snacksack.normaliser.SpoonsNormaliser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;

@Slf4j
@Service
public class SpoonsRequestHandler extends BaseRequestHandler {
    @Autowired
    private SpoonsClient spoonsClient;

    @Autowired
    private SpoonsNormaliser normaliser;

    public Answer handleSpoonsRequest(int moneyPence, int locationId, int threadedThreshold) throws JsonProcessingException {
        final Answer cachedAnswer = this.jedisClient.getAnswer(Restaurant.SPOONS, locationId, moneyPence);
        if (cachedAnswer.totalCalories != -1) {
            log.info("Cached answer present, returning value");
            return cachedAnswer;
        }

        final Set<NormalisedProduct> normalisedProducts = getProducts(locationId);
        final Answer answer = this.getAnswer(moneyPence, threadedThreshold, normalisedProducts);
        this.jedisClient.setAnswer(Restaurant.SPOONS, locationId, moneyPence, answer);
        return answer;
    }

    private Set<NormalisedProduct> getProducts(int locationId) throws JsonProcessingException {
        final Set<NormalisedProduct> products = jedisClient
                .getProducts(Restaurant.SPOONS, locationId);

        if (!products.isEmpty()) {
            log.info("Cache hit, returning products");
            return products;
        } else {
            log.info("Cache miss, fetching data from source");
            final URI uri = spoonsClient.constructURI(locationId);
            final SpoonsApiMenuData menuData = spoonsClient.getMenuResponse(uri);
            final Set<NormalisedProduct> fetchedProducts = spoonsClient.getProducts(menuData);
            jedisClient.setProducts(Restaurant.SPOONS, locationId, fetchedProducts);
            return fetchedProducts;
        }
    }

}
