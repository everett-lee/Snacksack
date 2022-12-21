package com.snacksack.snacksack.dp.topDown;

import com.snacksack.snacksack.model.NormalisedProduct;
import lombok.Data;

import java.util.List;

@Data
public class RecursiveHelperResult {
    final int totalHere;
    final List<NormalisedProduct> solutionProducts;
}
