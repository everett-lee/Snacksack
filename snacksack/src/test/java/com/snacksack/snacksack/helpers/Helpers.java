package com.snacksack.snacksack.helpers;

import com.snacksack.snacksack.model.NormalisedProduct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helpers {
    public static String readFileAsString(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public static List<NormalisedProduct> testCaseFileToProductList(String file) throws IOException {
        final String stringFile = readFileAsString(file);
        List<String[]> calorieValuePairs = Arrays.stream(stringFile.split("\n")).map(item -> item.split(" ")).toList();
        return IntStream.range(0, calorieValuePairs.size())
                .mapToObj(i ->  {
                    float price = Float.parseFloat(calorieValuePairs.get(i)[0]);
                    int calories = Integer.parseInt(calorieValuePairs.get(i)[1]);
                    return new NormalisedProduct(String.format("item%s", i), calories, price);
                })
                .collect(Collectors.toList());
    }
}
