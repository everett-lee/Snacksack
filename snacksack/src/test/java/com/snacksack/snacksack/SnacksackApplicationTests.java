package com.snacksack.snacksack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "redis.enabled=false" })
class SnacksackApplicationTests {

	@Test
	void contextLoads() {
	}

}
