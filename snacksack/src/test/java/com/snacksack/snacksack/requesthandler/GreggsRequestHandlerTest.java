package com.snacksack.snacksack.requesthandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.dp.bottomUp.BottomUpSolver;
import com.snacksack.snacksack.dp.bottomUp.BottomUpSolverThreaded;
import com.snacksack.snacksack.jedisclient.JedisClient;
import com.snacksack.snacksack.menuclient.GreggsClient;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.Restaurant;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.normaliser.NandosNormaliser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class GreggsRequestHandlerTest {
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
    private GreggsClient greggsClient;
    @InjectMocks
    private GreggsRequestHandler greggsRequestHandler;

    @BeforeEach
    public void setUp() {
        this.greggsRequestHandler.setLocationIds(Set.of(1));
    }

    @Test
    public void testCacheHit() throws JsonProcessingException {
        Set<NormalisedProduct> products = Set.of(new NormalisedProduct("Name", 1, 2));
        when(jedisClient.getProducts(Restaurant.GREGGS, 1)).thenReturn(products);
        Answer answer = greggsRequestHandler.handleGreggsRequest(55, 1, 5000);
        assertThat(answer.getNormalisedProducts().size(), is(1));
        // Assert client was not used due to cache hit
        verify(greggsClient, never()).constructURI(1);
    }

    @Test
    public void testCacheMiss() throws JsonProcessingException {
        Set<NormalisedProduct> products = Set.of(new NormalisedProduct("Name", 1, 2));
        when(jedisClient.getProducts(Restaurant.GREGGS, 1)).thenReturn(Set.of());
        when(greggsClient.getProducts(any())).thenReturn(products);
        Answer answer = greggsRequestHandler.handleGreggsRequest(55, 1, 5000);
        assertThat(answer.getNormalisedProducts().size(), is(1));
        // Assert jedis client invoked due to cache miss
        verify(jedisClient, Mockito.times(1)).setProducts(Restaurant.GREGGS, 1, products);
    }

}