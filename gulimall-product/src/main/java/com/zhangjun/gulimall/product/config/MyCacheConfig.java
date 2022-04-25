package com.zhangjun.gulimall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author 张钧
 * @Description
 * @create 2022-04-20 16:06
 */
@EnableCaching
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {

    /**
     * 会覆盖配置文件中的配置
     * @return
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        CacheProperties.Redis redis = cacheProperties.getRedis();
        if(redis.getTimeToLive() != null){
            config = config.entryTtl(redis.getTimeToLive());
        }
        if(redis.getKeyPrefix() != null){
            config = config.prefixKeysWith(redis.getKeyPrefix());
        }
        if(!redis.isCacheNullValues()){
            config = config.disableCachingNullValues();
        }
        if(!redis.isUseKeyPrefix()){
            config = config.disableKeyPrefix();
        }

        return config;
    }
}
