package com.snacksack.snacksack.dp;

import com.snacksack.snacksack.dp.topDown.TopDownSolver;
import com.snacksack.snacksack.helpers.Helpers;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.answer.Answer;
import com.snacksack.snacksack.model.answer.AnswerProduct;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class TopDownSolverTest {
    final Solver solver = new TopDownSolver();

    @Test
    public void testSingleItemCaseWhenFits() {
        Set<NormalisedProduct> products = Set.of(new NormalisedProduct("Item1", 55, 2502));
        Answer answer = solver.solve(2502, products);
        assertThat(answer.getTotalCalories(), is(equalTo(55)));
        assertThat(answer.getTotalCost(), is(equalTo(25.02)));
        assertThat(answer.getNormalisedProducts(), is(equalTo(products
                .stream()
                .map(AnswerProduct::new)
                .toList()
        )));
    }

    @Test
    public void testSingleItemCaseWhenNotFits() {
        Set<NormalisedProduct> products = Set.of(new NormalisedProduct("Item1", 55, 2503));
        Answer answer = solver.solve(2502, products);
        assertThat(answer.getTotalCalories(), is(equalTo(0)));
        assertThat(answer.getTotalCost(), is(equalTo(0.0)));
        assertThat(answer.getNormalisedProducts(), is(equalTo(List.of())));
    }

    @Test
    public void testMultipleItemsBasicCase() {
        List<NormalisedProduct> products = List.of(
                new NormalisedProduct("Item1", 800, 100),
                new NormalisedProduct("Item2", 400, 200),
                new NormalisedProduct("Item3", 0, 300),
                new NormalisedProduct("Item4", 500, 200),
                new NormalisedProduct("Item5", 200, 200)
        );
        Answer answer = solver.solve(400, new HashSet<>(products));
        assertThat(answer.getTotalCalories(), is(equalTo(1300)));
        assertThat(new HashSet<>(answer.getNormalisedProducts()), is(equalTo(
                Set.of(
                        new AnswerProduct(products.get(3)),
                        new AnswerProduct(products.get(0))
                )
        )));
        assertThat(answer.getTotalCost(), is(equalTo(3.0)));
    }

    @Test
    public void testMultipleItemsBasicCaseTwo() {
        List<NormalisedProduct> products = List.of(
                new NormalisedProduct("Item1", 200, 1644),
                new NormalisedProduct("Item2", 700, 6500),
                new NormalisedProduct("Item3", 450, 3700),
                new NormalisedProduct("Item4", 271, 2218),
                new NormalisedProduct("Item5", 202, 2226)
        );
        Answer answer = solver.solve(8144, new HashSet<>(products));
        assertThat(answer.getTotalCalories(), is(equalTo(923)));
        assertThat(new HashSet<>(answer.getNormalisedProducts()), is(equalTo(
                Set.of(
                        new AnswerProduct(products.get(4)),
                        new AnswerProduct(products.get(3)),
                        new AnswerProduct(products.get(2))
                )
        )));
        assertThat(answer.getTotalCost(), is(equalTo(81.44)));
    }

    @Test
    public void testMultipleItemsCaseOne() throws IOException {
        List<NormalisedProduct> testProds = Helpers.testCaseFileToProductList("src/test/resources/ks-test-case-1");
        Answer answer = solver.solve(200000, new HashSet<>(testProds));
        assertThat(answer.getTotalCalories(), is(equalTo(8085)));
        assertThat(answer.getTotalCost(), is(equalTo(1998.0)));
    }

    @Test
    public void testMultipleItemsCaseTwo() throws IOException {
        List<NormalisedProduct> testProds = Helpers.testCaseFileToProductList("src/test/resources/ks-test-case-2");
        Answer answer = solver.solve(200000, new HashSet<>(testProds));
        assertThat(answer.getTotalCalories(), is(equalTo(2210)));
        assertThat(answer.getTotalCost(), is(equalTo(1998.0)));
    }

    @Test
    public void testMultipleItemsCaseThree() throws IOException {
        List<NormalisedProduct> testProds = Helpers.testCaseFileToProductList("src/test/resources/ks-test-case-3");
        Answer answer = solver.solve(200000, new HashSet<>(testProds));
        assertThat(answer.getTotalCalories(), is(equalTo(475)));
        assertThat(answer.getTotalCost(), is(equalTo(1939.0)));
    }
}