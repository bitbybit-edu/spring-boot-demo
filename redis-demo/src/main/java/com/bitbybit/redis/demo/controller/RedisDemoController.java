package com.bitbybit.redis.demo.controller;

import com.bitbybit.redis.demo.constant.CacheConstant;
import com.bitbybit.redis.demo.entity.JpaUser;
import com.bitbybit.redis.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
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

    private Integer throughDatabaseCount = 0;

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
        Object o = redisTemplate.opsForValue().get(CacheConstant.JPA_USER + id);
        JpaUser jpaUser;
        if (Objects.isNull(o)) {
            synchronized (throughDatabaseCount) {
                throughDatabaseCount += 1;
                logger.info("through database count = {}", throughDatabaseCount);
            }
            Optional<JpaUser> byId = userRepository.findById(id);
            jpaUser = (byId.isPresent() ? byId.get() : null);
            Thread.sleep(new Double(Math.random() * 1000).longValue());
            redisTemplate.opsForValue().set(CacheConstant.JPA_USER + id, jpaUser);
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
        Object o = redisTemplate.opsForValue().get(CacheConstant.JPA_USER + id);
        JpaUser jpaUser;
        if (Objects.isNull(o)) {
            String uuid = UUID.randomUUID().toString();
            try {

                if (redisTemplate.opsForValue().setIfAbsent(CacheConstant.JPA_USER_LOCK + id, uuid, 10, TimeUnit.SECONDS)) {
                    logger.info("uuid = {} get lock", uuid);
                    Optional<JpaUser> byId = userRepository.findById(id);
                    jpaUser = (byId.isPresent() ? byId.get() : null);
                    redisTemplate.opsForValue().set(CacheConstant.JPA_USER + id, jpaUser);
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
                    if (uuid.equals(redisTemplate.opsForValue().get(CacheConstant.JPA_USER_LOCK + id))) {
                        redisTemplate.delete(CacheConstant.JPA_USER_LOCK + id);
                        logger.info("uuid = {} release lock", uuid);
                    } else {
                        logger.error("uuid = {} release fail lock invalid", uuid);
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

    @PostMapping("del/{id}")
    public void del(@PathVariable Long id) {
        synchronized (throughDatabaseCount) {
            redisTemplate.delete(CacheConstant.JPA_USER + id);
            throughDatabaseCount = 0;
        }
    }

    /**
     * 测试scan
     */
    @ResponseBody
    @PostMapping("scan/{pattern}")
    public List<String> scan(@PathVariable String pattern) throws IOException {
        List<String> keys = new ArrayList<>(1 >> 6);

        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern + "*").count(100).build();
        Cursor<byte[]> scan = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);
        scan.forEachRemaining(key -> {
            keys.add(new String(key, StandardCharsets.UTF_8));
        });
        return keys;
    }
}
