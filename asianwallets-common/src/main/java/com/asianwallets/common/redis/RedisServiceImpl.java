package com.asianwallets.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String REQUESTID = "1";
    public static final int RETRY_TIMES = Integer.MAX_VALUE;

    public static final long SLEEP_MILLIS = 25;//单位毫秒

    @Autowired(required = false)
    private JedisPool jedisPool;

    @Override
    public Jedis getResource() {
        return jedisPool.getResource();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void returnResource(Jedis jedis) {
        if (null != jedis) jedisPool.returnResourceObject(jedis);
    }

    @Override
    public String get(String key) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.get(key);
            //log.info("Redis get success - " + key + ", value:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + result);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public void set(String key, String value, long expire) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            String nxxx = "NX";
            if (jedis.exists(key)) {
                nxxx = "XX";
            }
            jedis.set(key, value, nxxx, "EX", expire);
            log.info("==========【Redis Set Success】========== key: {}, value: {}, expire: {}", key, value, expire);
        } catch (Exception e) {
            log.info("Redis set error: " + e.getMessage() + " - " + key + ", value:" + value + ", expire:" + expire);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.set(key, value);
            log.info("Redis set success - key: " + key + ", value: " + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis set error: " + e.getMessage() + " - " + key + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.lpush(key, value);
            log.info("Redis lpush success - " + key + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis lpush error: " + e.getMessage() + " - " + key + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void rpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.rpush(key, value);
            log.info("Redis rpush success - " + key + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis rpush error: " + e.getMessage() + " - " + key + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public String lpop(String key) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.lpop(key);
            log.info("Redis lpop success - " + key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis lpop error: " + e.getMessage() + " - " + key);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public String rpop(String key) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpop(key);
            log.info("Redis rpop success - " + key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis rpop error: " + e.getMessage() + " - " + key);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public void zadd(String key, long score, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.zadd(key, score, value);
            log.info("Redis zadd success - " + key + ", score:" + score + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis zadd error: " + e.getMessage() + " - " + key + ", score:" + score + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public List<String> zrange(String key, int start, int stop) {
        Set<String> results = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            results = jedis.zrange(key, start, stop);
            log.info("Redis zrange success - " + key + ", start:" + start + ", stop:" + stop);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis zrange error: " + e.getMessage() + " - " + key + ", start:" + start + ", stop:" + stop);
        } finally {
            returnResource(jedis);
        }
        return new ArrayList<String>(results);
    }

    @Override
    public void zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.zrem(key, value);
            log.info("Redis zrem success - " + key + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis zrem error: " + e.getMessage() + " - " + key + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.hset(key, field, value);
            log.info("Redis hset success - " + key + ", field:" + field + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis hset error: " + e.getMessage() + " - " + key + ", field:" + field + ", value:" + value);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public String hget(String key, String field) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hget(key, field);
            log.info("Redis hget success - " + key + ", field:" + field);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis hget error: " + e.getMessage() + " - " + key + ", field:" + field);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public Set<String> hkeys(String key) {
        Set<String> result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hkeys(key);
            log.info("Redis hkeys success - " + key);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis hkeys error: " + e.getMessage() + " - " + key);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    @Override
    public void hdel(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            jedis.hdel(key, field);
            log.info("Redis hdel success - " + key + ", field:" + field);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis hdel error: " + e.getMessage() + " - " + key + ", value:" + field);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    public void add(String key, Integer count) {
        String countStr = (null == count) ? "0" : count.toString();
        set(key, countStr);
    }

    @Override
    public long incr(String key) {
        return incr(key, -1);
    }

    @Override
    public long incr(String key, long value, int expire) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (!jedis.exists(key)) {
                jedis.set(key, "1");
                if (-1 != expire) {
                    jedis.expire(key, expire);
                }
            } else {
                value = jedis.incr(key);
            }
            log.info("Redis incr success - " + key + ", value:" + value);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Redis incr error: " + e.getMessage() + " - " + key + ", value:" + value);
        } finally {
            returnResource(jedis);
            return value;
        }
    }

    @Override
    public long incr(String key, int expire) {
        return incr(key, 1, expire);
    }

    /**
     * 尝试获取分布式锁
     * //     * @param jedis Redis客户端
     *
     * @param lockKey    锁
     *                   //     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    @Override
    public boolean lock(String lockKey, int expireTime) {
        return lock(lockKey, expireTime, RETRY_TIMES, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String lockKey, int expireTime, int retryTimes, long sleepMillis) {
        Jedis jedis = null;
        while (retryTimes-- > 0) {
            try {
                jedis = getResource();
                String result = jedis.set(lockKey, REQUESTID, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
            } catch (Exception e) {
                log.error("tryGetDistributedLock->key: {} ;error msg: {}" + lockKey, e.getMessage());
                return false;
            } finally {
                returnResource(jedis);
            }
        }
        return false;
    }

    /**
     * 释放分布式锁
     * //     * @param jedis Redis客户端
     *
     * @param lockKey 锁
     *                //     * @param requestId 请求标识
     * @return 是否释放成功
     */
    @Override
    public boolean releaseLock(String lockKey) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (!jedis.exists(lockKey)) {
                log.error("releaseDistributedLock-> key: {},不存在", lockKey);
                return true;
            }
            if (jedis.del(lockKey) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("releaseDistributedLock-> key:{}; error msg:{}", lockKey, e.getMessage());
            return false;
        } finally {
            returnResource(jedis);
        }
    }
}
