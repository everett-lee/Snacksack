package com.snacksack.snacksack.normaliser;

import com.snacksack.snacksack.model.ApiMenuData;
import com.snacksack.snacksack.model.NormalisedProduct;

import java.util.Set;

public interface Normaliser <T extends ApiMenuData> {
    Set<NormalisedProduct> getNormalisedProducts(T apiMenuData);
}
