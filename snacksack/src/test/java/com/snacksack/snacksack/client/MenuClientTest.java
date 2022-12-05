package com.snacksack.snacksack.client;

import com.snacksack.snacksack.helpers.Helpers;
import com.snacksack.snacksack.model.MenuResponse;
import com.snacksack.snacksack.model.Product;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MenuClientTest {
    @Test
    public void a() throws IOException, InterruptedException {
        //given:
        String responseJson = Helpers.readFileAsString("src/test/resources/menu-reduced.json");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        MenuClient menuClient = new MenuClient(httpClient);

        //and:
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseJson);


        //when:
        MenuResponse response = menuClient.getMenuResponse(1);

        //then:
        List<Product> products = response.getMenus().stream().flatMap(menu -> {
            return menu.getSubMenu().stream().flatMap(subMenu -> {
                return subMenu.getProductGroups().stream().flatMap(productGroup -> {
                    return productGroup.getProducts().stream();
                });
            });
        }).toList();
        assertThat(products.size(), is(equalTo(8)));
    }
}

