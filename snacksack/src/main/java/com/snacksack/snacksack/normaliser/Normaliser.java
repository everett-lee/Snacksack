package com.snacksack.snacksack.normaliser;

import com.snacksack.snacksack.model.ApiMenuData;
import com.snacksack.snacksack.model.NormalisedProduct;

import java.util.Set;

public interface Normaliser<T extends ApiMenuData> {

    /**
     * Interface for converting a ApiMenuData subclass representing parsed JSON
     * into a set of products with normalised structure. These serve as input
     * to the dynamic programming solver
     *
     * @param apiMenuData parsed representation of JSON menu data
     * @return a Set of normalised products
     */
    Set<NormalisedProduct> getNormalisedProducts(T apiMenuData);
}
