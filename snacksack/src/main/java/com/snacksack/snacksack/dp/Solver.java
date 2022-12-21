package com.snacksack.snacksack.dp;

import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.answer.Answer;

import java.util.List;
import java.util.Set;

public interface Solver {
    Answer EMPTY_ANSWER = new Answer(0, List.of());
    Answer solve(int totalMoney, Set<NormalisedProduct> products);
}