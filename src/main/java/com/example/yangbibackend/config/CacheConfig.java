package com.example.yangbibackend.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * SpringCache中配置 RedisCache
 *
 * @author xlwang55
 */
@EnableCaching
@Configuration
public class CacheConfig {
    /**
     * 测试缓存key
     */
    public static final String STUDENT_CACHE = "studentCache";
    public static final String MESSAGE_CACHE = "messageCache";

    /**
     * 设置SpringCache缓存时间:默认策略和自定义指定key策略的超时时间
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                this.getRedisCacheConfigurationWithTtl(60),
                this.getRedisCacheConfigurationMap());
    }

    /**
     * 自定义指定key策略的超时时间
     *
     * @author xlwang55
     * @date 2022/12/12
     */
    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>(2);
        //SsoCache和BasicDataCache进行过期时间配置
        redisCacheConfigurationMap.put(MESSAGE_CACHE, this.getRedisCacheConfigurationWithTtl(30 * 60));
        //自定义设置缓存时间
        redisCacheConfigurationMap.put(STUDENT_CACHE, this.getRedisCacheConfigurationWithTtl(60));
        return redisCacheConfigurationMap;
    }

    /**
     * 初始化缓存序列化配置和过期时间
     *
     * @param seconds 缓存时间
     * @author xlwang55
     * @date 2022/12/12
     */
    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return redisCacheConfiguration.serializeValuesWith(RedisSerializationContext
                .SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer)).entryTtl(Duration.ofSeconds(seconds));
    }

}

