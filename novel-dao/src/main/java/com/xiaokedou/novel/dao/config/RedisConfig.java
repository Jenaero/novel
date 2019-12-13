package com.xiaokedou.novel.dao.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.List;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {


    @Bean(name = "jedisPoolConfig")
    @Primary
    JedisPoolConfig getJedisPoolConfig() {
        return new JedisPoolConfig();
    }


    @Bean(name = "jedisPool")
    JedisPool getJedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig,
                           @Value("${spring.redis.host}") String host,
                           @Value("${spring.redis.port}") Integer port,
                           @Value("${spring.redis.timeout}") Integer timeout,
                           @Value("${spring.redis.password}") String password) {
        return new JedisPool(jedisPoolConfig, host, port, timeout, StringUtils.isEmpty(password) ? null : password);
    }


    @Bean(name = "shardJedisPool")
    ShardedJedisPool getShardJedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig,
                                       @Value("${spring.redis.host}") String host,
                                       @Value("${spring.redis.port}") Integer port,
                                       @Value("${spring.redis.timeout}") Integer timeout,
                                       @Value("${spring.redis.password}") String password) {

        List <JedisShardInfo> jedisShardInfos = Lists.newArrayList();
        JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, timeout);

        if (StringUtils.isNotEmpty(password)) {
            jedisShardInfo.setPassword(password);
        }

        jedisShardInfos.add(jedisShardInfo);

        return new ShardedJedisPool(jedisPoolConfig, jedisShardInfos);
    }
}
