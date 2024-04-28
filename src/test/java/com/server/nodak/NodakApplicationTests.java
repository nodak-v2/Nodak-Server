package com.server.nodak;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
class NodakApplicationTests {
	@Autowired
	Environment env;

	@Test
	@DisplayName("빌드 실패용 테스트")
	void contextLoads() {
		String property = env.getProperty("spring.application.name");
		System.out.println("========================");
		System.out.println(property);
		System.out.println("========================");
	}

}
