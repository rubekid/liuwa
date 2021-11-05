package com.liuwa.framework.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.concurrent.TimeUnit;

/**
 * Guava 缓存配置
 */
@Configuration
@EnableCaching
public class GuavaCacheConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 最大值
     */
    @Value("${guava.cache.maximumSize:1024}")
    private long maximumSize;

    /**
     * 过期时间 （秒） 默认10分钟
     */
    @Value("${guava.cache.expireAfterAccess:600}")
    private long expireAfterAccess;

    @Bean
    public CacheBuilder<Object, Object> cacheBuilder(){
        return CacheBuilder.newBuilder()
                .recordStats() // 开启缓存统计
                .concurrencyLevel(10) // 设置并发级别
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
    }

    @DependsOn({"cacheBuilder"})
    @Bean
    public Cache getCache(CacheBuilder<Object, Object> cacheBuilder){
        return cacheBuilder.build();
    }


}
