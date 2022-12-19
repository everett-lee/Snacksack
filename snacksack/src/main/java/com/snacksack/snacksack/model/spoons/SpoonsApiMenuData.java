package com.snacksack.snacksack.model.spoons;

import com.snacksack.snacksack.model.ApiMenuData;
import lombok.Data;

@Data
public class SpoonsApiMenuData extends ApiMenuData {
    final MenuResponse menuResponse;

    public SpoonsApiMenuData(MenuResponse menuResponse) {
        this.menuResponse = menuResponse;
    }
}
