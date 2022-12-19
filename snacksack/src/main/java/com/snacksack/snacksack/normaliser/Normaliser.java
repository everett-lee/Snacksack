package com.snacksack.snacksack.normaliser;

import com.snacksack.snacksack.model.ApiMenuData;
import com.snacksack.snacksack.model.NormalisedProduct;

import java.util.List;

public interface Normaliser <T extends ApiMenuData> {
    List<NormalisedProduct> getNormalisedProducts(T apiMenuData);
}
