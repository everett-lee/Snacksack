package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.snacksack.snacksack.menuclient.NandosClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.nandos.NandosApiMenuData;
import com.snacksack.snacksack.normaliser.NandosNormaliser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Set;

@Slf4j
@Service
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
            log.info("Cache hit, returning products");
            return products;
        } else {
            log.info("Cache miss, fetching data from source");
            final URI uri = nandosClient.constructURI();
            final NandosApiMenuData menuData = nandosClient.getMenuResponse(uri);
            final Set<NormalisedProduct> fetchedProducts = nandosClient.getProducts(menuData);
            jedisClient.setProducts(Restaurant.NANDOS, fetchedProducts);
            return fetchedProducts;
        }
    }

}
