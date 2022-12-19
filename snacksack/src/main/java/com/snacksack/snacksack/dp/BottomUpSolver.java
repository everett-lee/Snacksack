package com.snacksack.snacksack.dp;

import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.answer.Answer;

import java.util.ArrayList;
import java.util.List;

public class BottomUpSolver implements Solver {

    @Override
    public Answer solve(int totalMoney, List<NormalisedProduct> products) {
        if (products.isEmpty()) {
            return EMPTY_ANSWER;
        }

        final int m = products.size();
        final int n = totalMoney + 1;

        final MemMatrix memMatrix = new MemMatrix(m, n);

        final long firstProductCost = products.get(0).getPrice();
        final int firstProductCalories = products.get(0).getCalories();

        // Case where only one item provided
        if (m == 1) {
            if (firstProductCost <= totalMoney) {
                return new Answer(firstProductCalories, products);
            } else {
                return EMPTY_ANSWER;
            }
        }

        this.iterateMatrix(memMatrix, products);

        final List<NormalisedProduct> answerProducts = this.getAnswerProducts(memMatrix, products, memMatrix.getAnswer());
        return new Answer(memMatrix.getAnswer(), answerProducts);
    }

    private void iterateMatrix(MemMatrix memMatrix, List<NormalisedProduct> products) {
        long firstProductCost = products.get(0).getPrice();
        int firstProductCalories = products.get(0).getCalories();

        final int m = memMatrix.nRows;
        final int n = memMatrix.nCols;

        for (int money = 0; money < n; money++) {
            if (money >= firstProductCost) {
                memMatrix.setCell(0, money, firstProductCalories);
            }
        }

        for (int i = 1; i < m; i++) {
            long productCost = products.get(i).getPrice();
            int productCalories = products.get(i).getCalories();

            for (int money = 0; money < n; money++) {
                if (money < productCost) {
                    memMatrix.setCell(i, money, memMatrix.getCell(i - 1, money));
                } else {
                    int take = productCalories + memMatrix.getCell(i - 1, money - productCost);
                    int leave = memMatrix.getCell(i - 1, money);
                    memMatrix.setCell(i, money, Math.max(take, leave));
                }
            }
        }
    }

    private List<NormalisedProduct> getAnswerProducts(MemMatrix memMatrix, List<NormalisedProduct> products, int answer) {
        final List<NormalisedProduct> answerProducts = new ArrayList<>();
        int i = memMatrix.nRows - 1;
        int moneyLeft = memMatrix.nCols - 1;
        long totalCalories = 0;

        while (i > 0) {
            final NormalisedProduct currentProduct = products.get(i);
            final int caloriesHere = memMatrix.getCell(i, moneyLeft);

            if (moneyLeft - currentProduct.getPrice() >= 0) {
                final int caloriesBefore = memMatrix.getCell(i - 1, moneyLeft - currentProduct.getPrice());

                // If the difference between calories here and the calories for the cell
                // corresponding to the total cost before this product was chosen is equal
                // to the products value, it was used for the final answer
                if (caloriesHere - caloriesBefore == currentProduct.getCalories()) {

                    answerProducts.add(currentProduct);
                    moneyLeft -= currentProduct.getPrice();
                    totalCalories += currentProduct.getCalories();
                }
            }
            i--;
        }

        // final case where first product accounts for difference between running total of
        // calories and the answer, meaning it should be included
        if (answer - totalCalories == products.get(0).getCalories()) {
            answerProducts.add(products.get(0));
        }

        return answerProducts;
    }
}

