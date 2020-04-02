package com.bitbybit.redis.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonTests {
    private static final Logger logger = LoggerFactory.getLogger(CommonTests.class);

    @Test
    public void mathRound() {

        System.out.println(new Double(Math.random() * 1000).longValue());
    }

    @Test
    public void getTime() {
        System.out.println(System.nanoTime());
        System.out.println(System.currentTimeMillis());
        logger.error("test logger error {}","ccc", new Exception("12321321"));
    }
}
