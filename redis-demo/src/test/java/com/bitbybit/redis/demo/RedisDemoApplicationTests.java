package com.bitbybit.redis.demo;

import com.bitbybit.redis.demo.entity.JpaUser;
import com.bitbybit.redis.demo.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
class RedisDemoApplicationTests {

	@Autowired
	@Qualifier("testService1")
	TestService testService1;

	@Autowired
	@Qualifier("testService2")
	TestService testService2;

	@Test
	void contextLoads() {
	}

	@Test
	void serviceTest() {
		System.out.println(testService1.hello());
		System.out.println(testService2.hello());

	}

	/**
	 * 需要启动项目
	 */
	@Test
	void controllerTest() {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		ResponseEntity<JpaUser> forEntity = testRestTemplate.getForEntity("http://127.0.0.1:8081/redis/jpaUser/database/1", JpaUser.class);
		boolean xxSuccessful = forEntity.getStatusCode().is2xxSuccessful();
		JpaUser body = forEntity.getBody();
		System.out.println(forEntity.getStatusCode().getReasonPhrase());
		System.out.println(xxSuccessful);
		System.out.println(body);
	}

}
