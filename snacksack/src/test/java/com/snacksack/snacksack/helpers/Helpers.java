package com.snacksack.snacksack.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Helpers {
    public static String readFileAsString(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}
