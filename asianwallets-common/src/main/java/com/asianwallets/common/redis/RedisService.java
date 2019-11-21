package com.asianwallets.common.redis;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public interface RedisService {
    Jedis getResource();
    void returnResource(Jedis jedis);
    String get(String key);
    void set(String key, String value);
    void set(String key, String value, long expire);
    void lpush(String key, String value);
    void rpush(String key, String value);
    String lpop(String key);
    String rpop(String key);
    void zadd(String key, long score, String value);
    List<String> zrange(String key, int start, int stop);
    void zrem(String key, String value);
    void hset(String key, String field, String value);
    String hget(String key, String field);
    Set<String> hkeys(String key);
    void  hdel(String key, String field);
    void add(String key, Integer count);
    long incr(String key);
    long incr(String key, int expire);
    long incr(String key, long value, int expire);

    boolean lock(String lockKey, int expireTime);
    boolean lock(String key, int expire, int retryTimes, long sleepMillis);
    boolean releaseLock(String lockKey);
}
