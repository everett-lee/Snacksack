package com.snacksack.snacksack.dp.bottomUp;

import com.snacksack.snacksack.model.NormalisedProduct;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class BottomUpSolverThreaded extends BottomUpSolver {

    private final ForkJoinPool commonPool;
    private int threshold = 5_000;

    public BottomUpSolverThreaded() {
        int nProcessors = Runtime.getRuntime().availableProcessors();
        this.commonPool = new ForkJoinPool(nProcessors);
    }

    public BottomUpSolverThreaded(int threshold) {
        int nProcessors = Runtime.getRuntime().availableProcessors();
        this.commonPool = new ForkJoinPool(nProcessors);
        this.threshold = threshold;
    }

    @Override
    protected void iterateMatrix(MemMatrix memMatrix, List<NormalisedProduct> products) {
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
            final NormalisedProduct product = products.get(i);

            final RecursiveRowUpdate recursiveRowUpdate = new RecursiveRowUpdate(
                    memMatrix,
                    this.threshold,
                    0,
                    memMatrix.nCols - 1,
                    i,
                    product
            );
            commonPool.invoke(recursiveRowUpdate);
        }
    }

}
