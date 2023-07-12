package com.yupi.springbootinit.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.api.RedissonClient;
/**
 * @author lanshu
 * @date 2023-07-12
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private Integer database;
    private Integer port;
    private String host;

    @Bean
    public RedissonClient redissonClient() {
        // 1. Create config object
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://" + host + ":"+port);
        RedissonClient redissonClient = Redisson.create();

        return redissonClient;
    }
}
