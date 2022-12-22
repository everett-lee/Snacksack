package com.snacksack.snacksack.model.nandos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@lombok.Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    Nandos nandos;
}
