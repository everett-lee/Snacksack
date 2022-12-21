package com.snacksack.snacksack.model.answer;

import com.snacksack.snacksack.model.NormalisedProduct;
import lombok.Data;

@Data
public class AnswerProduct {
    public final String name;
    public final int calories;
    public final double price;

    public AnswerProduct(NormalisedProduct product) {
        this.name = product.name;
        this.calories = product.calories;
        this.price = product.price / 100.0;
    }
}
