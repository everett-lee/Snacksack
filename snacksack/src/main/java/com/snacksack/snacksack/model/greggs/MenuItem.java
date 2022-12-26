package com.snacksack.snacksack.model.greggs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuItem {
    String internalDescription;
    double eatOutPrice;
    List<NutritionalItem> nutritionalValues;
}
