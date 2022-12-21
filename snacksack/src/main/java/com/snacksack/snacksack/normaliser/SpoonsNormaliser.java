package com.snacksack.snacksack.normaliser;

import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.spoons.MenuResponse;
import com.snacksack.snacksack.model.spoons.Product;
import com.snacksack.snacksack.model.spoons.SpoonsApiMenuData;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpoonsNormaliser implements Normaliser<SpoonsApiMenuData> {
    @Override
    public Set<NormalisedProduct> getNormalisedProducts(SpoonsApiMenuData apiMenuData) {
        final MenuResponse menuResponse = apiMenuData.getMenuResponse();
        final List<Product> products = menuResponse.getMenus().stream().flatMap(menu -> {
            return menu.getSubMenu().stream().flatMap(subMenu -> {
                return subMenu.getProductGroups().stream().flatMap(productGroup -> {
                    return productGroup.getProducts().stream();
                });
            });
        }).toList();
        return normalise(products);
    }

    private Set<NormalisedProduct> normalise(List<Product> products) {
        return products.stream().map(product -> {
            final String name = product.getDisplayName();
            final int calories = product.getCalories();
            final float price = product.getPriceValue();
            return new NormalisedProduct(name, calories, price);
        }).collect(Collectors.toSet());
    }
}
