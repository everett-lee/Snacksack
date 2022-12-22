package com.snacksack.snacksack.model.nandos;

import com.snacksack.snacksack.model.ApiMenuData;
import lombok.Data;

@Data
public class NandosApiMenuData extends ApiMenuData {
    final com.snacksack.snacksack.model.nandos.MenuResponse menuResponse;

    public NandosApiMenuData(MenuResponse menuResponse) {
        this.menuResponse = menuResponse;
    }
}
