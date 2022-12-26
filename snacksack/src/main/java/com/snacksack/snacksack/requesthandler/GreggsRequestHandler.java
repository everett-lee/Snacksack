package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.api.exceptions.InvalidLocationException;
import com.snacksack.snacksack.menuclient.GreggsClient;
import com.snacksack.snacksack.model.Location;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.greggs.GreggsApiMenuData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GreggsRequestHandler extends BaseRequestHandler {
    @Value("classpath:greggs-locations.json")
    private Resource locationsFile;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GreggsClient greggsClient;

    private List<Location> greggsLocations;

    private Set<Integer> locationIDs;

    public Answer handleGreggsRequest(int moneyPence, int locationId, int threadedThreshold) throws JsonProcessingException {
        final Set<NormalisedProduct> normalisedProducts = getProducts(locationId);
        return this.getAnswer(moneyPence, threadedThreshold, normalisedProducts);
    }

    private Set<NormalisedProduct> getProducts(int locationId) throws JsonProcessingException {
        if (!this.getLocationIDs().contains(locationId)) {
            throw new InvalidLocationException(
                    "Location id is not recognised"
            );
        }

        final Set<NormalisedProduct> products = jedisClient
                .getProducts(Restaurant.GREGGS, locationId);

        if (!products.isEmpty()) {
            log.info("Cache hit, returning products");
            return products;
        } else {
            log.info("Cache miss, fetching data from source");
            final URI uri = greggsClient.constructURI(locationId);
            final GreggsApiMenuData menuData = greggsClient.getMenuResponse(uri);
            final Set<NormalisedProduct> fetchedProducts = greggsClient.getProducts(menuData);
            jedisClient.setProducts(Restaurant.GREGGS, locationId, fetchedProducts);
            return fetchedProducts;
        }
    }

    @PostConstruct
    public void initLocations() throws IOException {
        this.greggsLocations = objectMapper
                .readValue(this.locationsFile.getInputStream(), new TypeReference<>() {
                });
        this.locationIDs = this.greggsLocations.stream()
                .map(Location::id).collect(Collectors.toSet());
    }

    public List<Location> getLocations() {
        return this.greggsLocations;
    }
    public Set<Integer> getLocationIDs() {
        return this.locationIDs;
    }

    public void setLocationIds(Set<Integer> locationIDs) {
        this.locationIDs = locationIDs;
    }
}
