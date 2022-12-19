package com.snacksack.snacksack.model;

import lombok.Data;

@Data
public class NormalisedProduct {
    String name;
    int calories;
    long price;

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
}
