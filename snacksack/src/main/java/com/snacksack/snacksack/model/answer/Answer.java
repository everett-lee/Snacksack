package com.snacksack.snacksack.model.answer;

import com.snacksack.snacksack.model.NormalisedProduct;
import lombok.Data;

import java.util.List;

@Data
public class Answer {
    public final int totalCalories;
    public final double totalCost;
    List<AnswerProduct> normalisedProducts;

    public Answer(int totalCalories, List<NormalisedProduct> normalisedProducts) {
        this.totalCalories = totalCalories;
        this.normalisedProducts = normalisedProducts
                .stream()
                .map(AnswerProduct::new).toList();
        this.totalCost = normalisedProducts
                .stream()
                .mapToLong(NormalisedProduct::getPrice).sum() / 100.0;
    }
}
