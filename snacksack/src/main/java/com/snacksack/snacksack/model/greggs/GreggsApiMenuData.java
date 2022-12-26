package com.snacksack.snacksack.model.greggs;

import com.snacksack.snacksack.model.ApiMenuData;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class GreggsApiMenuData extends ApiMenuData {
    final List<MenuItem> menuItems;

    public GreggsApiMenuData(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public GreggsApiMenuData() {
        this.menuItems = new ArrayList<>();
    }
}
