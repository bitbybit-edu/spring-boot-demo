package com.bitbybit.redis.demo.constant;

/**
 * @author liulin
 */
public class CacheConstant {

    /**
     * redis 测试缓存击穿前缀
     */
    public static final String JPA_USER = "jpa:user:";

    /**
     * redis 测试解决缓存击穿使用互斥锁
     */
    public static final String JPA_USER_LOCK = JPA_USER + "lock:";

}
