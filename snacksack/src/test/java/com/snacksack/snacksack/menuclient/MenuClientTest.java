package com.snacksack.snacksack.menuclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.helpers.Helpers;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.greggs.GreggsApiMenuData;
import com.snacksack.snacksack.model.nandos.NandosApiMenuData;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MenuClientTest {

    @Test
    public void testSpoonsClientGet() throws IOException, InterruptedException {
        //given:
        String responseJson = Helpers.readFileAsString("src/test/resources/menu-reduced.json");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        SpoonsClient menuClient = new SpoonsClient(new ObjectMapper(), httpClient);
        URI uri = menuClient.constructURI(9);

        //and:
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseJson);

        //when:
        SpoonsApiMenuData response = menuClient.getMenuResponse(uri);

        //then:
        Set<NormalisedProduct> products = menuClient.getProducts(response);
        assertThat(products.size(), is(equalTo(8)));

    }

    @Test
    public void testSpoonsClientGetFullMenu() throws IOException, InterruptedException {
        //given:
        String responseJson = Helpers.readFileAsString("src/test/resources/menu.json");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        SpoonsClient menuClient = new SpoonsClient(new ObjectMapper(), httpClient);
        URI uri = menuClient.constructURI(9);

        //and:
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseJson);

        //when:
        SpoonsApiMenuData response = menuClient.getMenuResponse(uri);

        Set<NormalisedProduct> products = menuClient.getProducts(response);
    }

    @Test
    public void testNandosClientGetFullMenu() throws IOException, InterruptedException {
        //given:
        String responseJson = Helpers.readFileAsString("src/test/resources/nandos-menu.json");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        NandosClient menuClient = new NandosClient(new ObjectMapper(), httpClient);
        URI uri = menuClient.constructURI();

        //and:
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseJson);

        //when:
        NandosApiMenuData response = menuClient.getMenuResponse(uri);
        Set<NormalisedProduct> products = menuClient.getProducts(response);
        assertThat(products.size(), is(equalTo(95)));
    }

    @Test
    public void testGreggsClientGet() throws IOException, InterruptedException {
        //given:
        String responseJson = Helpers.readFileAsString("src/test/resources/greggs-menu.json");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        GreggsClient menuClient = new GreggsClient(new ObjectMapper(), httpClient);
        URI uri = menuClient.constructURI(92);

        //and:
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseJson);

        //when:
        GreggsApiMenuData response = menuClient.getMenuResponse(uri);

        //then:
        assertThat(uri.toString(), is(equalTo("https://production-digital.greggs.co.uk/api/v1.0/articles/shop/0092")));
        Set<NormalisedProduct> products = menuClient.getProducts(response);
        assertThat(products.size(), is(greaterThan(0)));
    }
}
