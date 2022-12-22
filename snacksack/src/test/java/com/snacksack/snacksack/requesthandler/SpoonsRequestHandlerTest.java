package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.dp.bottomUp.BottomUpSolver;
import com.snacksack.snacksack.dp.bottomUp.BottomUpSolverThreaded;
import com.snacksack.snacksack.jedisclient.JedisClient;
import com.snacksack.snacksack.menuclient.SpoonsClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.normaliser.NandosNormaliser;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class SpoonsRequestHandlerTest {
    @Spy
    protected Solver bottomUpSolver = new BottomUpSolver();

    @Spy
    protected Solver bottomUpSolverThreaded = new BottomUpSolverThreaded();

    @Spy
    protected ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    private NandosNormaliser normaliser = new NandosNormaliser();
    @Mock
    protected JedisClient jedisClient;
    @Mock
    private SpoonsClient spoonsClient;
    @InjectMocks
    private SpoonsRequestHandler spoonsRequestHandler;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCacheHit() throws JsonProcessingException {
        Set<NormalisedProduct> products = Set.of(new NormalisedProduct("Name", 1, 2));
        when(jedisClient.getProducts(Restaurant.SPOONS, 1)).thenReturn(products);
        Answer answer = spoonsRequestHandler.handleSpoonsRequest(55, 1, 5000);
        assertThat(answer.getNormalisedProducts().size(), is(1));
        // Assert client was not used due to cache hit
        verify(spoonsClient, never()).constructURI(1);
    }

    @Test
    public void testCacheMiss() throws JsonProcessingException {
        Set<NormalisedProduct> products = Set.of(new NormalisedProduct("Name", 1, 2));
        when(jedisClient.getProducts(Restaurant.SPOONS, 1)).thenReturn(Set.of());
        when(spoonsClient.getProducts(any())).thenReturn(products);
        Answer answer = spoonsRequestHandler.handleSpoonsRequest(55, 1, 5000);
        assertThat(answer.getNormalisedProducts().size(), is(1));
        // Assert jedis client invoked due to cache miss
        verify(jedisClient, Mockito.times(1)).setProducts(Restaurant.SPOONS, 1, products);
    }

}