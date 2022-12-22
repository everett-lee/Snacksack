package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.jedisclient.JedisClient;
import com.snacksack.snacksack.menuclient.NandosClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.nandos.NandosApiMenuData;
import com.snacksack.snacksack.normaliser.NandosNormaliser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.Set;

@Slf4j
public class NandosRequestHandler extends BaseRequestHandler {

    @Autowired
    private NandosClient nandosClient;

    @Autowired
    private NandosNormaliser normaliser;


    public Answer handleNandosRequest(int moneyPence, int threadedThreshold) throws JsonProcessingException {
        final Set<NormalisedProduct> normalisedProducts = getProducts();
        return this.getAnswer(moneyPence, threadedThreshold, normalisedProducts);
    }

    private Set<NormalisedProduct> getProducts() throws JsonProcessingException {
        final Set<NormalisedProduct> products = jedisClient.getProducts(Restaurant.NANDOS);

        if (!products.isEmpty()) {
            return products;
        } else {
            final URI uri = nandosClient.constructURI();
            final NandosApiMenuData menuData = nandosClient.getMenuResponse(uri);
            final Set<NormalisedProduct> fetchedProducts = nandosClient.getProducts(menuData);
            jedisClient.setProducts(Restaurant.NANDOS, fetchedProducts);
            return fetchedProducts;
        }
    }

}
