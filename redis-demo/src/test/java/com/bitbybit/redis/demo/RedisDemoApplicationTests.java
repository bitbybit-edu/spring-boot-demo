package com.bitbybit.redis.demo;

import com.bitbybit.redis.demo.entity.JpaUser;
import com.bitbybit.redis.demo.service.TestService;
import com.bitbybit.redis.demo.service.TestServiceFactory;
import com.bitbybit.redis.demo.service.impl.TestService1Impl;
import com.bitbybit.redis.demo.service.impl.TestService2Impl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class RedisDemoApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(RedisDemoApplicationTests.class);

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

    /**
     * ioc 测试
     */
//    @Test
//    void iocTest() {
//        DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
//        TestService testService1 = defaultListableBeanFactory.getBean("testService1", TestService.class);
//        System.out.println(testService1.hello());
//
//    }

    /**
     * 简单工厂方法测试和spring boot 关系不大
     */
    @Test
    void testFactoryTest() {
        try {
            TestService testService1 = TestServiceFactory.createTestService(TestService1Impl.class.getName());
            logger.info(testService1.hello());
            TestService testService2 = TestServiceFactory.createTestService(TestService2Impl.class.getName());
            logger.info(testService2.hello());

        } catch (Exception e) {

        }
    }

}
