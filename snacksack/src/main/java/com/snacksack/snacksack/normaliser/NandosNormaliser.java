
package com.snacksack.snacksack.normaliser;

import com.snacksack.snacksack.model.NormalisedProduct;
import com.snacksack.snacksack.model.nandos.Item;
import com.snacksack.snacksack.model.nandos.NandosApiMenuData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NandosNormaliser implements Normaliser<NandosApiMenuData> {
    @Override
    public Set<NormalisedProduct> getNormalisedProducts(NandosApiMenuData apiMenuData) {
        final Set<NormalisedProduct> normalisedProducts = apiMenuData
                .getMenuResponse()
                .getResult()
                .getData()
                .getNandos()
                .getMenu()
                .getSections()
                .stream().flatMap(section -> section.getItems().stream())
                .filter(this::itemHasValues)
                .filter(item -> !item.getNutritionalInfo().getFactsForPortionSizes().isEmpty() )
                .map(this::normalise)
                .collect(Collectors.toSet());

        log.info("Normalised {} products", normalisedProducts.size());
        return normalisedProducts;
    }

    private NormalisedProduct normalise(Item item) {
        String name = item.getDisplayName();
        // Simplify by just getting calories for first portion size
        int calories = item.getNutritionalInfo().getFactsForPortionSizes().get(0).getEnergyKcal();
        int price = item.getPrice().getValue();
        return new NormalisedProduct(name, calories, price);
    }

    private boolean itemHasValues(Item item) {
        return item.getPrice() != null && item.getDisplayName() != null && item.getNutritionalInfo() != null;
    }
}
