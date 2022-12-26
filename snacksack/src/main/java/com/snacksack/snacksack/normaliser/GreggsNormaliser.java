package com.snacksack.snacksack.normaliser;

import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.greggs.GreggsApiMenuData;
import com.snacksack.snacksack.model.greggs.MenuItem;
import com.snacksack.snacksack.model.greggs.NutritionalItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GreggsNormaliser implements Normaliser<GreggsApiMenuData> {
    @Override
    public Set<NormalisedProduct> getNormalisedProducts(GreggsApiMenuData apiMenuData) {
        final List<MenuItem> menuItems = apiMenuData.getMenuItems();
        final Set<NormalisedProduct> normalisedProducts = menuItems.stream()
                .filter(item -> item.getEatOutPrice() > 0)
                .map(this::replaceNutritionalInfo)
                .filter(item -> !item.getNutritionalValues().isEmpty())
                .map(this::normalise)
                .collect(Collectors.toSet());
        log.info("Normalised {} products", normalisedProducts.size());
        return normalisedProducts;
    }

    private MenuItem replaceNutritionalInfo(MenuItem item) {
        final List<NutritionalItem> filteredNutritionalItems = item.getNutritionalValues()
                .stream().filter(nutritionalItem -> "Calories".equals(nutritionalItem.getName()))
                .toList();
        return new MenuItem(item.getInternalDescription(), item.getEatOutPrice(), filteredNutritionalItems);
    }

    private NormalisedProduct normalise(MenuItem item) {
        final String name = item.getInternalDescription();
        final int calories = item.getNutritionalValues().get(0).getValue();
        final double price = item.getEatOutPrice();
        return new NormalisedProduct(name, calories, price);
    }
}
