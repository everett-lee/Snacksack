package com.snacksack.snacksack.client;

import com.snacksack.snacksack.model.MenuResponse;
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

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
public class MenuClient extends AbstractClient {

    public MenuClient(HttpClient client) {
        super(client);
    }

    public MenuResponse getMenuResponse(int locationId) throws HttpClientErrorException {
        final URI uri = constructURI(locationId);
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.of(10, SECONDS))
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                log.error("Request failed with response code {}", res.statusCode());
                throw new RuntimeException();
            }
            return objectMapper.readValue(res.body(), MenuResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("Failed to GET menu response. Returned status: {}", e.getStatusCode());
            throw e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URI constructURI(int locationId) {
        final Map<String, Integer> pathVars = Map.of(
                "locationId", locationId
        );

        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(BASE_ENDPOINT);
        return uriBuilder.buildAndExpand(pathVars).toUri();
    }
}
