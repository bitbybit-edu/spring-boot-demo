package com.bitbybit.redis.demo.controller;

import com.bitbybit.redis.demo.entity.JpaUser;
import com.bitbybit.redis.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author liulin
 */
@RestController
@RequestMapping("redis")
public class RedisDemoController {

    private static final Logger logger = LoggerFactory.getLogger(RedisDemoController.class);

    String driverClassName = "com.mysql.cj.jdbc.Driver";

    String url = "jdbc:mysql://47.93.233.111:3306/db_example";

    String username = "liulin";

    String password = "123456";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserRepository userRepository;

    @ResponseBody
    @PostMapping("jpaUser/save")
    public JpaUser save(@RequestParam String name, @RequestParam String email) {
        JpaUser n = new JpaUser();
        n.setName(name);
        n.setEmail(email);
        JpaUser save = userRepository.save(n);
        return save;
    }

    @GetMapping("jpaUser/{id}")
    public JpaUser findById(@PathVariable Long id) throws InterruptedException {
        Object o = redisTemplate.opsForValue().get("jpa:user:" + id);
        JpaUser jpaUser;
        if (Objects.isNull(o)) {
            Optional<JpaUser> byId = userRepository.findById(id);
            jpaUser = (byId.isPresent() ? byId.get() : null);
            Thread.sleep(new Double(Math.random() * 1000).longValue());
            redisTemplate.opsForValue().set("jpa:user:" + id, jpaUser);
        } else {
            jpaUser = (JpaUser) o;
        }
        return jpaUser;
    }

    @GetMapping("jpaUser/database/{id}")
    public JpaUser findByIdDatabase(@PathVariable Long id) throws InterruptedException {
        Optional<JpaUser> byId = userRepository.findById(id);
        return byId.get();
    }

    @GetMapping("database/connection")
    public void databaseConnection() throws InterruptedException {
        Connection connection = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            logger.error("connection fail", e);
        } finally {
            try {
                if (!Objects.isNull(connection)) {
                    connection.close();
                }
            } catch (Exception e) {
                logger.error("connection close fail", e);
            }
        }

    }


    @GetMapping("jpaUser/mutexLock/{id}")
    public JpaUser mutexLockFindById(@PathVariable Long id) throws InterruptedException {
        Object o = redisTemplate.opsForValue().get("jpa:user:" + id);
        JpaUser jpaUser;
        if (Objects.isNull(o)) {
            String uuid = UUID.randomUUID().toString();
            try {

                if (redisTemplate.opsForValue().setIfAbsent("jpa:user:lock:" + id, uuid, 10, TimeUnit.SECONDS)) {
                    logger.info("uuid = {} get lock", uuid);
                    Optional<JpaUser> byId = userRepository.findById(id);
                    jpaUser = (byId.isPresent() ? byId.get() : null);
                    Thread.sleep(new Double(Math.random() * 1000).longValue());
                    redisTemplate.opsForValue().set("jpa:user:" + id, jpaUser);
                } else {
                    logger.info("uuid = {} not get lock", uuid);
                    Thread.sleep(100L);
                    jpaUser = mutexLockFindById(id);
                }

            } catch (Exception e) {
                jpaUser = null;
                logger.error("get user fail", e);
            } finally {
                try {
                    if (uuid.equals(redisTemplate.opsForValue().get("jpa:user:lock:" + id))) {
                        redisTemplate.delete("jpa:user:lock:" + id);
                        logger.info("uuid = {} release lock", uuid);
                    } else {
                        logger.error("uuid = {} relesse fail lock expired", uuid);
                    }
                } catch (Exception e) {
                    logger.error("release lock fail", e);
                }

            }

        } else {
            jpaUser = (JpaUser) o;
        }
        return jpaUser;
    }
}
