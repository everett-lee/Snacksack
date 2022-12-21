package com.snacksack.snacksack.dp.topDown;

import com.snacksack.snacksack.dp.Solver;
import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.answer.Answer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@Qualifier("TopDownSolver")
public class TopDownSolver implements Solver {

    @Override
    public Answer solve(int totalMoney, Set<NormalisedProduct> products) {
        final Map<MemoKey, RecursiveHelperResult> mem = new HashMap<>();
        final RecursiveHelperResult result = this.recursiveHelper(0, totalMoney, products.stream().toList(), mem);
        return new Answer(result.totalHere, result.solutionProducts);
    }

    private RecursiveHelperResult recursiveHelper(int i, long money, List<NormalisedProduct> products, Map<MemoKey, RecursiveHelperResult> mem) {
        final MemoKey keyHere = new MemoKey(money, i);
        if (mem.containsKey(keyHere)) {
            return mem.get(keyHere);
        }

        if (money <= 0 || i >= products.size()) {
            return new RecursiveHelperResult(0, List.of());
        }

        final NormalisedProduct productHere = products.get(i);
        final int caloriesHere = productHere.getCalories();
        final long priceHere = productHere.getPrice();

        RecursiveHelperResult take = new RecursiveHelperResult(0, List.of());
        if (money >= priceHere) {
            RecursiveHelperResult previous = this.recursiveHelper(i + 1, money - priceHere, products, mem);
            List<NormalisedProduct> solutionProducts = Stream
                    .concat(previous.solutionProducts.stream(), Stream.of(productHere)).toList();
            take = new RecursiveHelperResult(previous.totalHere + caloriesHere, solutionProducts);
        }
        RecursiveHelperResult leave = this.recursiveHelper(i + 1, money, products, mem);

        if (take.getTotalHere() > leave.getTotalHere()) {
            mem.put(keyHere, take);
            return take;
        } else {
            mem.put(keyHere, leave);
            return leave;
        }
    }
}