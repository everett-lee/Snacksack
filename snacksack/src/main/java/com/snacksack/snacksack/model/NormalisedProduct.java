package com.snacksack.snacksack.model;

import lombok.Data;

@Data
public class NormalisedProduct {
    public final String name;
    public final int calories;
    public final long price;

    public NormalisedProduct(String name, int calories, int price) {
        this.name = name;
        this.calories = calories;
        this.price = price;
    }

    public NormalisedProduct(String name, int calories, double price) {
        this.name = name;
        this.calories = calories;
        this.price = Math.round(price * 100);
    }

    // for Jackson
    public NormalisedProduct() {
        this.name = "";
        this.calories = 0;
        this.price = 0;
    }
}
