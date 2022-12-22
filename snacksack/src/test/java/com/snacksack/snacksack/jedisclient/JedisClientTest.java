package com.snacksack.snacksack.jedisclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.helpers.Helpers;
import com.snacksack.snacksack.menuclient.NandosClient;
import com.snacksack.snacksack.menuclient.SpoonsClient;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.nandos.NandosApiMenuData;
import com.snacksack.snacksack.model.nandos.Section;
import com.snacksack.snacksack.model.spoons.Menu;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
class JedisClientTest {
    @Container
    public static final GenericContainer REDIS = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);

    public static Jedis JEDIS;
    public static JedisClient JEDIS_CLIENT;

    static {
        REDIS.setWaitStrategy(Wait.defaultWaitStrategy()
            .withStartupTimeout(Duration.of(30, SECONDS)));
    }

    @BeforeAll()
    public static void setUp() throws InterruptedException {
        JEDIS = new Jedis(REDIS.getHost(), REDIS.getFirstMappedPort());
        JEDIS_CLIENT = new JedisClient(JEDIS, new ObjectMapper());
    }

    @Test
    public void testSetAndGetNandos() throws IOException, InterruptedException {
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
        JEDIS_CLIENT.setMenuData(Restaurant.NANDOS, response);
        final NandosApiMenuData menuData = (NandosApiMenuData) JEDIS_CLIENT.getMenuData(Restaurant.NANDOS).orElse(null);

        assertThat(menuData, is(notNullValue()));
        List<Section> sections = menuData.getMenuResponse().getResult().getData().getNandos().getMenu().getSections();
        assertThat(sections.size(), greaterThan(0));
    }

    @Test
    public void getNandosEmpty() throws IOException {
        final NandosApiMenuData menuData = (NandosApiMenuData) JEDIS_CLIENT.getMenuData(Restaurant.NANDOS).orElse(null);
        assertThat(menuData, is(nullValue()));
    }

    @Test
    public void testSetAndGetSpoons() throws IOException, InterruptedException {
        //given:
        String responseJson = Helpers.readFileAsString("src/test/resources/menu.json");
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        SpoonsClient menuClient = new SpoonsClient(new ObjectMapper(), httpClient);
        URI uri = menuClient.constructURI(9);
        int restaurantId = 5;

        //and:
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseJson);

        //when:
        SpoonsApiMenuData response = menuClient.getMenuResponse(uri);
        JEDIS_CLIENT.setMenuData(Restaurant.SPOONS, restaurantId, response);
        final SpoonsApiMenuData menuData = (SpoonsApiMenuData) JEDIS_CLIENT.getMenuData(Restaurant.SPOONS, restaurantId).orElse(null);

        assertThat(menuData, is(notNullValue()));
        List<Menu> menus = menuData.getMenuResponse().getMenus();
        assertThat(menus.size(), greaterThan(0));
    }

    @Test
    public void getSpoonsEmpty() throws IOException {
        final SpoonsApiMenuData menuData = (SpoonsApiMenuData) JEDIS_CLIENT.getMenuData(Restaurant.SPOONS, 0).orElse(null);
        assertThat(menuData, is(nullValue()));
    }
}