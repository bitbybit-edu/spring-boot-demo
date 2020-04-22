package com.bitbybit.redis.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
        logger.error("test logger error {}", "ccc", new Exception("12321321"));
    }

    @Test
    public void ifElse() {
        String a = "sd";
        String b = null;

        if (!Objects.isNull(a)) {
            logger.info(a);
        } else if (Objects.isNull(b)) {
            logger.info(b);
        }
    }

    @Test
    public void bubbleSort() {
        int[] arr = new int[]{4, 3, 2, 1};
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            logger.info("arr = {}", arr);
        }
    }
}
