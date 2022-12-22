package com.snacksack.snacksack.menuclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.nandos.MenuResponse;
import com.snacksack.snacksack.model.nandos.NandosApiMenuData;
import com.snacksack.snacksack.normaliser.NandosNormaliser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;


@Slf4j
public class NandosClient extends AbstractClient<NandosApiMenuData> {
    public NandosClient(ObjectMapper objectMapper, HttpClient client) {
        super(
                objectMapper,
                client,
                "https://www.nandos.co.uk/food/menu/page-data/index/page-data.1669896001149.json",
                new NandosNormaliser()
        );
    }

    @Override
    public NandosApiMenuData getMenuResponse(URI uri) throws HttpClientErrorException {
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.of(10, SECONDS))
                .uri(uri)
                .GET()
                .build();

        try {
            log.info("Fetching data from nandos at URI {}", uri);
            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                log.error("Request failed with response code {}", res.statusCode());
                throw new RuntimeException();
            }
            final MenuResponse menuResponse = objectMapper.readValue(res.body(), MenuResponse.class);
            log.info("Data fetched");
            return new NandosApiMenuData(menuResponse);
        } catch (HttpClientErrorException e) {
            log.error("Failed to GET menu response. Returned status: {}", e.getStatusCode());
            throw e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public URI constructURI() {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(this.baseEndpoint);
        return uriBuilder.build().toUri();
    }

    @Override
    public Set<NormalisedProduct> getProducts(NandosApiMenuData apiMenuData) {
        return this.normaliser.getNormalisedProducts(apiMenuData);
    }
}
