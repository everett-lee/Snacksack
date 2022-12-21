package com.snacksack.snacksack.dp.bottomUp;

import com.snacksack.snacksack.model.NormalisedProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class RecursiveRowUpdate extends RecursiveAction {
    private final MemMatrix memMatrix;
    private final int threshold;
    private final int start;
    private final int finish;
    private final int i;
    private final NormalisedProduct normalisedProduct;

    public RecursiveRowUpdate(
            MemMatrix memMatrix,
            int threshold,
            int start,
            int finish,
            int i,
            NormalisedProduct product
    ) {
        this.memMatrix = memMatrix;
        this.threshold = threshold;
        this.start = start;
        this.finish = finish;
        this.i = i;
        this.normalisedProduct = product;
    }

    @Override
    protected void compute() {
        if (finish - start > this.threshold) {
            ForkJoinTask.invokeAll(this.createSubtasks());
        } else {
            this.process();
        }
    }

    private List<RecursiveRowUpdate> createSubtasks() {
        final List<RecursiveRowUpdate> subtasks = new ArrayList<>();
        final int midPoint = this.start + (this.finish - this.start) / 2;

        subtasks.add(new RecursiveRowUpdate(this.memMatrix, this.threshold, start, midPoint, this.i, this.normalisedProduct));
        subtasks.add(new RecursiveRowUpdate(this.memMatrix, this.threshold, midPoint + 1, this.finish, this.i, this.normalisedProduct));

        return subtasks;
    }

    private void process() {
        final long productCost = normalisedProduct.getPrice();
        final int productCalories = normalisedProduct.getCalories();

        for (int money = this.start; money <= this.finish; money++) {
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
