package com.bitbybit.redis.demo.service;

import com.bitbybit.redis.demo.service.impl.TestService1Impl;
import org.springframework.util.Assert;

/**
 * @author liulin
 */
public class TestServiceFactory {

    /**
     *
     * @param
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static TestService createTestService(String className) throws Exception {
        TestService testService;
        Object o = Class.forName(className).newInstance();
//        Assert.isInstanceOf(TestService.class, o);
        if (TestService.class.isInstance(o)) {
            testService = (TestService) o;
        } else {
            throw new Exception("类型转换错误");
        }
        return testService;
    }


}
