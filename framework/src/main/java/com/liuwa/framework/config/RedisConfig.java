package com.liuwa.framework.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.liuwa.common.config.JacksonObjectMapper;
import com.liuwa.framework.serializer.PrefixRedisSerializer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * redis配置
 * 
 * @author liuwa
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 缓存时长
     */
    private Duration timeToLive = Duration.ofHours(2);

    /**
     * 键名前缀
     */
    @Value("${spring.redis.prefix:}")
    private String keyPrefix;

    /**
     * 数据库索引 dbindex
     */
    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);


        Jackson2JsonRedisSerializer serializer = getJsonRedisSerializer();

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new PrefixRedisSerializer(keyPrefix));
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new PrefixRedisSerializer(keyPrefix));
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer serializer = getJsonRedisSerializer();

        String prefix = StringUtils.isNotEmpty(keyPrefix) ? (keyPrefix + ":") : "";

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> prefix + cacheName)
                .entryTtl(timeToLive)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        final Map<String, RedisCacheConfiguration> cacheConfigurations = getInitialCacheConfigurations();
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
        return cacheManager;
    }


    @Bean
    public DefaultRedisScript<Long> limitScript()
    {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }


    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }


    /**
     * 限流脚本
     */
    private String limitScriptText()
    {
        return "local key = KEYS[1]\n" +
                "local count = tonumber(ARGV[1])\n" +
                "local time = tonumber(ARGV[2])\n" +
                "local current = redis.call('get', key);\n" +
                "if current and tonumber(current) > count then\n" +
                "    return tonumber(current);\n" +
                "end\n" +
                "current = redis.call('incr', key)\n" +
                "if tonumber(current) == 1 then\n" +
                "    redis.call('expire', key, time)\n" +
                "end\n" +
                "return tonumber(current);";
    }

    /**
     * 获取序列化器
     * @return
     */
    private Jackson2JsonRedisSerializer getJsonRedisSerializer(){
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = new JacksonObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        serializer.setObjectMapper(mapper);
        return serializer;
    }

    /**
     * 获取初始化缓存配置
     * @return
     */
    private Map<String, RedisCacheConfiguration> getInitialCacheConfigurations() {
        final String prefix = StringUtils.isNotEmpty(keyPrefix) ? (keyPrefix + ":") : "";
        final Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Component.class);
        final List<CacheConfig> collect = beansWithAnnotation.entrySet().stream().flatMap(entry -> {
            try {
                final Object value = entry.getValue();
                // 获得原本的class名字，spring代理的都是后面有$$直接截取即可
                String className = "";
                if (value.getClass().getName().contains("$")){
                    className = value.getClass().getName().
                            substring(0, value.getClass().getName().indexOf("$"));
                }else{
                    className = value.getClass().getName();
                }

                // 获得原始的字节码文件，如果被spring 代理之后，方法上会获取不到注解信息
                final Method[] methods = Class.forName(className)
                        .getDeclaredMethods();

                return Arrays.stream(methods).flatMap(method -> {
                    String[] cacheNames = null;
                    CacheDuration cacheDuration = method.getAnnotation(CacheDuration.class);
                    Cacheable cacheable = method.getAnnotation(Cacheable.class);
                    CachePut cachePut = method.getAnnotation(CachePut.class);
                    if(cacheDuration == null){
                        return null;
                    }
                    if (cacheable != null) {
                        cacheNames = ArrayUtils.addAll(cacheable.cacheNames(), cacheable.value());
                    }
                    else if(cachePut != null){
                        cacheNames = ArrayUtils.addAll(cachePut.cacheNames(), cachePut.value());
                    }

                    if(cacheNames == null || cacheNames.length == 0){
                        return null;
                    }

                    String ttfs = cacheDuration.value();

                    return Arrays.stream(cacheNames).map(name -> {

                        final CacheConfig cacheConfig = new CacheConfig();
                        cacheConfig.setName(name);
                        if("-1".equals(ttfs)){
                            cacheConfig.setTtl(Duration.ZERO);
                            return cacheConfig;
                        }
                        if(!ttfs.matches("\\d[smhd]")){
                            logger.warn("当前配置不符合规范，ttf 必须是-1 或以s,m,h,d结尾 -> {}", ttfs);
                            return null;
                        }
                        final int time = Integer.parseInt(ttfs.substring(0, ttfs.length() - 1));
                        if (ttfs.endsWith("s")) {
                            cacheConfig.setTtl(Duration.ofSeconds(time));
                        }
                        else if (ttfs.endsWith("m")) {
                            cacheConfig.setTtl(Duration.ofMinutes(time));
                        }
                        else if (ttfs.endsWith("h")) {
                            cacheConfig.setTtl(Duration.ofHours(time));
                        } else if (ttfs.endsWith("d")) {
                            cacheConfig.setTtl(Duration.ofDays(time));
                        }
                        return cacheConfig;
                    }).filter(Objects::nonNull);
                });


            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return null;
            }
        }).collect(Collectors.toList());

        return collect.stream().collect(Collectors.toMap(CacheConfig::getName, p -> {
            Jackson2JsonRedisSerializer serializer = getJsonRedisSerializer();

            return RedisCacheConfiguration.defaultCacheConfig()
                    .computePrefixWith(cacheName -> prefix + cacheName)
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                    .entryTtl(p.getTtl());
        }, (key1, key2) -> key2));
    }


    /**
     * 缓存配置 name#ttl
     */
    class CacheConfig {

        /**
         * 名称
         */
        private String name;

        /**
         * 过期时间
         */
        private Duration ttl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
    }

}
