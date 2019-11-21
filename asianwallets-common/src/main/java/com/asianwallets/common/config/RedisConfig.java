package com.asianwallets.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.time}")
    private int time;

    @Value("${redis.database}")
    private int database;

    @Value("${redis.pool.max-idle}")
    private int maxIdle;

    @Value("${redis.pool.min-idle}")
    private int minIdle;

    @Value("${redis.pool.max-total}")
    private int maxTotal;

    @Value("${redis.pool.max-wait-millis}")
    private long maxWaitMillis;

    @Bean
    public JedisPoolConfig getRedisConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMillis);
        return config;
    }

    @Bean(name = "jedisPool", autowire = Autowire.BY_NAME)
    public JedisPool getJedisPool(){
        JedisPoolConfig config = getRedisConfig();
        //需要密码
        JedisPool pool = null;
        if (StringUtils.isEmpty(password)) {
            pool = new JedisPool(config,host,port);
        } else {
            pool = new JedisPool(config, host, port, time, password,database);
        }
        log.info("init JredisPool ...");
        return pool;
    }

}
