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
        RecursiveHelperResult result = this.recursiveHelper(0, totalMoney, products.stream().toList(), mem);
        int finalTotal = result.totalHere;

        List<NormalisedProduct> answerProducts = new ArrayList<>();
        while (result != null) {
            if (result.product != null) {
                answerProducts.add(result.product);
            }
            result = result.previous;
        }

        return new Answer(finalTotal, answerProducts);
    }

    private RecursiveHelperResult recursiveHelper(
            int i,
            long money,
            List<NormalisedProduct> products,
            Map<MemoKey, RecursiveHelperResult> mem)
    {
        final MemoKey keyHere = new MemoKey(money, i);
        if (mem.containsKey(keyHere)) {
            return mem.get(keyHere);
        }

        if (money <= 0 || i >= products.size()) {
            return new RecursiveHelperResult(0, null, null);
        }

        final NormalisedProduct productHere = products.get(i);
        final int caloriesHere = productHere.getCalories();
        final long priceHere = productHere.getPrice();

        RecursiveHelperResult take = null;
        RecursiveHelperResult previousLeave = this.recursiveHelper(i + 1, money, products, mem);
        RecursiveHelperResult leave = new RecursiveHelperResult(previousLeave.totalHere, previousLeave, null);
        if (money >= priceHere) {
            RecursiveHelperResult previousTake = this.recursiveHelper(i + 1, money - priceHere, products, mem);
            take = new RecursiveHelperResult(previousTake.totalHere + caloriesHere, previousTake, productHere);
        } else {
            take = leave;
        }

        if (take.getTotalHere() > leave.getTotalHere()) {
            mem.put(keyHere, take);
            return take;
        } else {
            mem.put(keyHere, leave);
            return leave;
        }
    }
}