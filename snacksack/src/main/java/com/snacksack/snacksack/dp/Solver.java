package com.snacksack.snacksack.dp;

import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.answer.Answer;

import java.util.List;
import java.util.Set;

public interface Solver {
    Answer EMPTY_ANSWER = new Answer(0, List.of());

    /**
     * Interface to solve the food version of the 0 1 knapsack problem,
     * or choosing products with the most combined calories given
     * a limit on money available.
     *
     * @param totalMoney money available to spend on products
     * @param products products available to choose from
     * @return an answer containing total calories, total price and products
     * selected
     */
    Answer solve(int totalMoney, Set<NormalisedProduct> products);
}