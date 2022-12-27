package com.snacksack.snacksack.jedisclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.helpers.Helpers;
import com.snacksack.snacksack.menuclient.NandosClient;
import com.snacksack.snacksack.menuclient.SpoonsClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.nandos.NandosApiMenuData;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.introspect.Annotated;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Set;

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

    public static JedisPool JEDIS_POOL;
    public static JedisClient JEDIS_CLIENT;

    static {
        REDIS.setWaitStrategy(Wait.defaultWaitStrategy()
                .withStartupTimeout(Duration.of(30, SECONDS)));
    }

    @BeforeAll()
    public static void setUp() throws InterruptedException {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        JEDIS_POOL = new JedisPool(poolConfig, REDIS.getHost(), REDIS.getFirstMappedPort());
        JEDIS_CLIENT = new JedisClient(JEDIS_POOL, new ObjectMapper());
    }

    @Test
    public void testSetAndGetNandosProducts() throws IOException, InterruptedException {
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
        JEDIS_CLIENT.setProducts(Restaurant.NANDOS, menuClient.getProducts(response));

        Set<NormalisedProduct> products = JEDIS_CLIENT
                .getProducts(Restaurant.NANDOS);


        assertThat(products, is(notNullValue()));
        assertThat(products.size(), greaterThan(0));
    }

    @Test
    public void getNandosProductsEmpty() throws IOException {
        final Set<NormalisedProduct> products = JEDIS_CLIENT
                .getProducts(Restaurant.NANDOS);
        assertThat(products, is(empty()));
    }

    @Test
    public void testSetAndGetSpoonsProducts() throws IOException, InterruptedException {
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
        JEDIS_CLIENT.setProducts(Restaurant.SPOONS, restaurantId, menuClient.getProducts(response));
        final Set<NormalisedProduct> products = JEDIS_CLIENT
                .getProducts(Restaurant.SPOONS, restaurantId);

        assertThat(products, is(notNullValue()));
        assertThat(products.size(), greaterThan(0));
    }

    @Test
    public void getSpoonsProductsEmpty() throws IOException {
        final Set<NormalisedProduct> products = JEDIS_CLIENT
                .getProducts(Restaurant.SPOONS, 0);
        assertThat(products, is(empty()));
    }

    @Test
    public void testSetAndGetNandosAnswer() throws IOException, InterruptedException {
        //Given:
        Answer answer = new Answer(33, List.of(
                new NormalisedProduct("A", 342, 34),
                new NormalisedProduct("B", 2535, 22)
        ));
        JEDIS_CLIENT.setAnswer(Restaurant.NANDOS, 440, answer);

        //When
        Answer cachedAnswer = JEDIS_CLIENT.getAnswer(Restaurant.NANDOS, 440);

        //Then
        assertThat(cachedAnswer, is(notNullValue()));
        assertThat(cachedAnswer, equalTo(answer));
    }

    @Test
    public void testSetAndGetNandosAnswerEmpty() throws IOException, InterruptedException {
        //Given
        Answer defaultAnswer = new Answer(-1, List.of());
        //When
        Answer cachedAnswer = JEDIS_CLIENT.getAnswer(Restaurant.NANDOS, 440);

        //Then
        assertThat(cachedAnswer, is(notNullValue()));
        assertThat(cachedAnswer, equalTo(defaultAnswer));
    }

    @Test
    public void testSetAndGetGreggsAnswer() throws IOException, InterruptedException {
        //Given:
        Answer answer = new Answer(33, List.of(
                new NormalisedProduct("A", 342, 34),
                new NormalisedProduct("B", 2535, 22)
        ));
        JEDIS_CLIENT.setAnswer(Restaurant.GREGGS, 42, 440, answer);

        //When
        Answer cachedAnswer = JEDIS_CLIENT.getAnswer(Restaurant.GREGGS, 42, 440);

        //Then
        assertThat(cachedAnswer, is(notNullValue()));
        assertThat(cachedAnswer, equalTo(answer));
    }

    @Test
    public void testSetAndGetGreggsAnswerEmpty() throws IOException, InterruptedException {
        //Given
        Answer defaultAnswer = new Answer(-1, List.of());
        //When
        Answer cachedAnswer = JEDIS_CLIENT.getAnswer(Restaurant.GREGGS, 13,440);

        //Then
        assertThat(cachedAnswer, is(notNullValue()));
        assertThat(cachedAnswer, equalTo(defaultAnswer));
    }
}