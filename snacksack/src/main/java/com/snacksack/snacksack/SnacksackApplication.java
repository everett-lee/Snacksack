package com.snacksack.snacksack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//curl https://static.wsstack.nn4maws.net/content/v3/menus/137.json  -H "APIKey: YVB5QfeRKUK1+EGvXGjPgQA93reRTUJHsCuQSHR+=="
// curl https://api.jdwetherspoon.com//api/v2/pubs/137/food  -H "APIKey: YVB5QfeRKUK1+EGvXGjPgQA93reRTUJHsCuQSHR+=="

@SpringBootApplication
public class SnacksackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnacksackApplication.class, args);
	}

}
