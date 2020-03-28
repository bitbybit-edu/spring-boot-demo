package com.bitbybit.demo.jpa;

import com.bitbybit.demo.jpa.entity.JpaUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest
class JpaDemoApplicationTests {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void addNewUser() throws URISyntaxException {
		URI uri = new URI("http://localhost:8080/jpa/demo/user/add?name=liulin&email=a");
		ResponseEntity<JpaUser> jpaUserResponseEntity = testRestTemplate.postForEntity(uri, null, JpaUser.class);
		JpaUser body = jpaUserResponseEntity.getBody();
		System.out.println(body);
	}

}
