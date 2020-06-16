package com.bitbybit.redis.demo.service.impl;

import com.bitbybit.redis.demo.service.TestService;
import org.springframework.stereotype.Service;

/**
 * @author liulin
 */
@Service("testService1")
public class TestService1Impl implements TestService {
    @Override
    public String hello() {
        return "test service 1";
    }
}
