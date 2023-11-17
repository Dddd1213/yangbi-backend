package com.example.yangbibackend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * todo:放入通用模板中
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

    private Integer database;

    private String host;

    private Integer port;

    private String password;

    @Bean
    public RedissonClient getRedissonClient() throws IOException {
        // 1. Create config object
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setPassword(password)
                .setAddress("redis://" + host + ":" + port);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
