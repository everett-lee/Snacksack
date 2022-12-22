package com.snacksack.snacksack.model.answer;

import com.snacksack.snacksack.model.NormalisedProduct;

public record AnswerProduct(String name, int calories, double price) {

    public AnswerProduct(NormalisedProduct product) {
        this(product.getName(), product.getCalories(), product.getPrice() / 100.0);
    }
}
