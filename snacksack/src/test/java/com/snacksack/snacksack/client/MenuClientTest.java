package com.snacksack.snacksack.client;

import com.snacksack.snacksack.dp.bottomUp.BottomUpSolver;
import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.helpers.Helpers;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
        SpoonsClient menuClient = new SpoonsClient(httpClient);
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
    public void testSpoonsClientGet2() throws IOException, InterruptedException {
        //given:
        String responseJson = Helpers.readFileAsString("src/test/resources/menu.json");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        SpoonsClient menuClient = new SpoonsClient(httpClient);
        URI uri = menuClient.constructURI(9);

        //and:
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseJson);

        //when:
        SpoonsApiMenuData response = menuClient.getMenuResponse(uri);

        Set<NormalisedProduct> products = menuClient.getProducts(response);
        Solver solver = new BottomUpSolver();
        Answer answer = solver.solve(1811, products);
        System.out.println(answer);

    }
}
