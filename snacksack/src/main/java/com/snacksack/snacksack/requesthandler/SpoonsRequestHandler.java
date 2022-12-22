package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.menuclient.SpoonsClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import com.snacksack.snacksack.normaliser.SpoonsNormaliser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.Set;

@Slf4j
public class SpoonsRequestHandler extends BaseRequestHandler {
    @Autowired
    private SpoonsClient spoonsClient;

    @Autowired
    private SpoonsNormaliser normaliser;


    public Answer handleSpoonsRequest(int moneyPence, int threadedThreshold, int restaurantId) throws JsonProcessingException {
        final Set<NormalisedProduct> normalisedProducts = getProducts(restaurantId);
        return this.getAnswer(moneyPence, threadedThreshold, normalisedProducts);
    }

    private Set<NormalisedProduct> getProducts(int restaurantId) throws JsonProcessingException {
        final Set<NormalisedProduct> products = jedisClient
                .getProducts(Restaurant.SPOONS, restaurantId);

        if (!products.isEmpty()) {
            return products;
        } else {
            final URI uri = spoonsClient.constructURI(restaurantId);
            final SpoonsApiMenuData menuData = spoonsClient.getMenuResponse(uri);
            final Set<NormalisedProduct> fetchedProducts = spoonsClient.getProducts(menuData);
            jedisClient.setProducts(Restaurant.SPOONS, restaurantId, fetchedProducts);
            return fetchedProducts;
        }
    }

}
