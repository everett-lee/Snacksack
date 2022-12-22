package com.snacksack.snacksack.model.nandos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.snacksack.snacksack.model.ApiMenuData;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NandosApiMenuData extends ApiMenuData {
    final com.snacksack.snacksack.model.nandos.MenuResponse menuResponse;

    public NandosApiMenuData(MenuResponse menuResponse) {
        this.menuResponse = menuResponse;
    }
    public NandosApiMenuData() {
        this.menuResponse = new MenuResponse();
    }
}
