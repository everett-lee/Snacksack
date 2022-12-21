package com.snacksack.snacksack.client;

import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.spoons.MenuResponse;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import com.snacksack.snacksack.normaliser.SpoonsNormaliser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;


@Slf4j
public class SpoonsClient extends AbstractClient<SpoonsApiMenuData> {
    public SpoonsClient(HttpClient client) {
        super(
                client,
                "https://static.wsstack.nn4maws.net/content/v3/menus/{locationId}.json",
                new SpoonsNormaliser()
        );
    }

    @Override
    public SpoonsApiMenuData getMenuResponse(URI uri) throws HttpClientErrorException {
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
            final MenuResponse menuResponse = objectMapper.readValue(res.body(), MenuResponse.class);
            log.info("Data fetched");
            return new SpoonsApiMenuData(menuResponse);
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
    public Set<NormalisedProduct> getProducts(SpoonsApiMenuData apiMenuData) {
        return this.normaliser.getNormalisedProducts(apiMenuData);
    }
}
