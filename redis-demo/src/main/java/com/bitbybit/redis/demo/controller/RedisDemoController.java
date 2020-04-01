package com.bitbybit.redis.demo.controller;

import com.bitbybit.redis.demo.entity.JpaUser;
import com.bitbybit.redis.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("redis")
public class RedisDemoController {

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
        Object o = redisTemplate.opsForValue().get(id.toString());
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

}
