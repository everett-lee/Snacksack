package com.snacksack.snacksack.menuclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.greggs.GreggsApiMenuData;
import com.snacksack.snacksack.model.greggs.MenuItem;
import com.snacksack.snacksack.normaliser.GreggsNormaliser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;


@Slf4j
public class GreggsClient extends AbstractClient<GreggsApiMenuData> {
    public GreggsClient(ObjectMapper objectMapper, HttpClient client) {
        super(
                objectMapper,
                client,
                "https://production-digital.greggs.co.uk/api/v1.0/articles/shop/{locationId}",
                new GreggsNormaliser()
        );
    }

    @Override
    public GreggsApiMenuData getMenuResponse(URI uri) throws HttpClientErrorException {
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.of(10, SECONDS))
                .uri(uri)
                .GET()
                .build();

        try {
            log.info("Fetching data from spoons at URI {}", uri);
            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                log.error("Request failed with response code {}", res.statusCode());
                throw new RuntimeException();
            }
            final List<MenuItem> menuItems = objectMapper.readValue(res.body(), new TypeReference<>() {
            });
            log.info("Data fetched");
            return new GreggsApiMenuData(menuItems);
        } catch (HttpClientErrorException e) {
            log.error("Failed to GET menu response. Returned status: {}", e.getStatusCode());
            throw e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public URI constructURI(int locationId) {
        final Map<String, Integer> pathVars = Map.of(
                "locationId", locationId
        );

        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.baseEndpoint);
        return uriBuilder.buildAndExpand(pathVars).toUri();
    }

    @Override
    public Set<NormalisedProduct> getProducts(GreggsApiMenuData apiMenuData) {
        return this.normaliser.getNormalisedProducts(apiMenuData);
    }
}
