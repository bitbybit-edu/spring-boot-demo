package com.bitbybit.redis.demo.service.impl;

import com.bitbybit.redis.demo.service.TestService;
import org.springframework.stereotype.Service;

/**
 * @author liulin
 */
@Service("testService2")
public class TestService2Impl implements TestService {
    @Override
    public String hello() {
        return "test service 2";
    }
}
