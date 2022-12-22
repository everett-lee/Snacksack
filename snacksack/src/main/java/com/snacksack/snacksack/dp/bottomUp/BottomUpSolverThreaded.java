package com.snacksack.snacksack.dp.bottomUp;

import com.snacksack.snacksack.model.NormalisedProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ForkJoinPool;

@Slf4j
@Service
public class BottomUpSolverThreaded extends BottomUpSolver {

    final int nProcessors = Runtime.getRuntime().availableProcessors();
    private final ForkJoinPool commonPool;
    private int threshold = 5_000;

    public BottomUpSolverThreaded() {
        this.commonPool = new ForkJoinPool(nProcessors);
    }

    public BottomUpSolverThreaded(int threshold) {
        this.commonPool = new ForkJoinPool(nProcessors);
        this.threshold = threshold;
    }

    @Override
    protected void iterateRow(int n, int i, MemMatrix memMatrix, NormalisedProduct product) {
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
