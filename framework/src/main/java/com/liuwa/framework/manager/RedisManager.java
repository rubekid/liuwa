package com.liuwa.framework.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuwa.common.config.JacksonObjectMapper;
import com.liuwa.common.core.redis.RedisCache;
import com.liuwa.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 管理器
 */
public class RedisManager {

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(RedisManager.class);

    private static RedisCache redisCache = SpringUtils.getBean(RedisCache.class);



    /**
     * 设置超时
     * @param key
     * @param seconds
     * @return
     */
    public static boolean expire(String key, int seconds) {
        return redisCache.expire(key, seconds);
    }

    /**
     * 设置过期
     * @param key
     * @param timestamp
     * @return
     */
    public static boolean expireAt(String key, long timestamp) {
        long now = System.currentTimeMillis();
        int diff = (int)(timestamp - now);
        if(diff <=0){
            return redisCache.deleteObject(key);
        }
        return redisCache.expire(key, diff, TimeUnit.MICROSECONDS);
    }

    /**
     * 获取剩余过期时间 -2 不存在 -1 没有设置过期时间
     * @param key
     * @return
     */
    public static long ttl(String key) {
        return redisCache.getExpire(key);

    }

    /**
     * 移除过期设置
     * @param key
     * @return
     */
    public static boolean persist(String key) {
        return redisCache.persist(key);
    }

    /**
     * 批量删除
     * @param prefix
     */
    public static void batchDeleteByKey(String prefix){
        Collection<String> set = redisCache.keys(prefix + "*");

        Iterator<String> it = set.iterator();
        List<String> keys = new ArrayList<String>();
        while(it.hasNext()){
            keys.add(it.next());
            if(keys.size() > 0 && keys.size() % 100 == 0){
                redisCache.deleteObject(keys);
                keys.clear();
            }

        }
        if(keys.size() > 0){
            redisCache.deleteObject(keys);
        }

    }



    /**
     * 通过key获取
     * @param key
     * @return
     */
    public static String get(String key, String defaultValue){
        String value = get(key);
        if(value == null){
            value = defaultValue;
        }
        return value;
    }

    /**
     * 通过key获取
     * @param key
     * @return
     */
    public static <T>T get(String key){
        return redisCache.getCacheObject(key);
    }

    /**
     * 保存
     * @param key
     * @param value
     */
    public static void set(String key, String value){
        redisCache.setCacheObject(key, value);
    }

    /**
     * 保存哈希类型
     * @param key
     * @param obj
     */
    public static void hset(String key, Object obj){
        redisCache.setCacheMap(key, toMap(obj));
    }

    /**
     * 设置某一字段
     * @param key
     * @param field
     * @param value
     */
    public static void hset(String key, String field, Object value) {
        redisCache.setCacheMapValue(key,field, value);
    }



    /**
     * 保存哈希类型 并设置过期时间
     * @param key
     * @param obj
     */
    public static void hsetExpireIn(String key, Object obj, int seconds){
        hset(key, obj);
        expire(key, seconds);
    }

    /**
     * 获取哈希表中某字段
     * @param key
     * @param field
     * @return
     */
    public static <T>T hget(String key, String field){
        return redisCache.getCacheMapValue(key, field);
    }

    /**
     * 删除哈希表中某字段
     * @param key
     * @param fields
     * @return
     */
    public static void hdel(String key, String ... fields){
        redisCache.hdel(key, fields);
    }

    /**
     * 通过哈希类型获取对象
     * @param key
     * @param clazz
     */
    public static <T> T  hgetAll(String key, Class<T> clazz){
        Map<String, String> hash = hgetAll(key);
        if(hash != null && hash.size() > 0){
            return parse(toJSONString(hash), clazz);
        }
        return null;
    }

    /**
     * 通过哈希类型获取对象
     * @param key
     */
    public static Map<String, String> hgetAll(String key){
        return redisCache.getCacheMap(key);
    }

    /**
     * 设置键的字符串值(指定超时)
     * @param key
     * @param data
     * @param seconds
     */
    public static void set(String key, Object data, int seconds){
        redisCache.setCacheObject(key, data, seconds, TimeUnit.SECONDS);
    }



    /**
     * 根据key 获取列表
     * @param key
     * @return
     */
    public static <T> List<T> findList(String key){
        return redisCache.getCacheList(key);
    }


    /**
     * 设置列表
     * @param key
     * @param list
     */
    public static <E> void setList(String key, List<E> list){
        redisCache.deleteObject(key);
        redisCache.setCacheList(key, list);
    }

    /**
     * 新增
     * @param key
     * @param item
     */
    public static void insert(String key, Object item){
        rpush(key, item);
    }

    /**
     * 从右侧新增
     * @param key
     * @param item
     */
    public static void append(String key, Object item) {
        rpush(key, item);
    }

    /**
     * 批量加入列表
     * @param <E>
     * @param key
     * @param items
     */
    public static <E> long batchAppend(String key, List<E> items) {
        if(items.size() > 0) {
            return redisCache.setCacheList(key, items);
        }
        return 0;
    }

    /**
     * rpush
     * @param key
     * @param item
     */
    public static long rpush(String key, Object item){
        return redisCache.rpush(key, item);
    }

    /**
     * lpush
     * @param key
     * @param item
     */
    public static long lpush(String key, Object item) {
        return  redisCache.lpush(key, item);
    }

    /**
     * 获取键值列表
     * @param pattern
     * @return
     */
    public static Set<String> keys(String pattern){
        Collection<String> collection = redisCache.keys(pattern);
        Set<String> keys = new HashSet<String>();
        return new HashSet<String>(collection);
    }

    /**
     * 设置值
     * @param key
     * @param obj
     */
    public static void set(String key, Object obj){
        redisCache.setCacheObject(key, obj);
    }



    /**
     * 增加值
     * @param key
     * @param incrValue
     */
    public static long  incrBy(String key, long incrValue){
        return redisCache.incrBy(key, incrValue);
    }

    /**
     * 增加值
     * @param key
     * @param incrValue
     */
    public static double incrBy(String key, double incrValue){
        return redisCache.incrBy(key, incrValue);
    }

    /**
     * 减少值
     * @param key
     * @param incrValue
     */
    public static long  decrBy(String key, long incrValue){
        return redisCache.decrBy(key, incrValue);
    }





    /**
     * 哈希表字段自增
     * @param key
     * @param field
     * @param value
     */
    public static long hincrBy(String key, String field, long value) {
        return redisCache.hincrBy(key, field, value);
    }

    /**
     * 哈希表字段自增
     * @param key
     * @param field
     * @param value
     */
    public static double hincrBy(String key, String field, double value) {
        return redisCache.hincrBy(key, field, value);
    }




    /**
     * 删除
     * @param key
     */
    public static boolean remove(String key){
        return redisCache.remove(key);
    }


    /**
     * 发布
     * @param channel
     * @param message
     */
    public void publish(String channel, Object message){
        redisCache.publish(channel, message);
    }


    /**
     * 根据key pop 对象
     * @param key
     * @return
     */
    public static <T> T pop(String key){
        return lpop(key);
    }

    /**
     * 根据key lpop 对象
     * @param key
     * @return
     */
    public static <T> T lpop(String key){
        return redisCache.lpop(key);
    }

    /**
     * 根据key rpop 对象
     * @param key
     * @return
     */
    public static <T> T rpop(String key){
        return redisCache.rpop(key);
    }


    /**
     * 根据key pop 对象
     * @param key
     * @param timeout 超时 （秒）
     * @return
     */
    public static  <T> T blpop(String key, int timeout){
        return redisCache.blpop(key, timeout);
    }

    /**
     * 根据key pop 对象
     * @param key
     * @return
     */
    public static <T> T blpop(String key){
        return redisCache.blpop(key);
    }

    /**
     * 移除列表中与参数 VALUE 相等的元素
     * @param key
     * @param value
     */
    public static void lrem(String key, Object value) {
        redisCache.lrem(key, value);
    }

    /**
     * 移除列表中与参数 VALUE 相等的元素
     * @param key
     * @param count
     * @param value
     */
    public static void lrem(String key, Integer count, Object value) {
        redisCache.lrem(key, count, value);
    }


    /**   集合   **/

    /**
     * 向集合添加成员
     */
    public static void sadd(String key, Object ... items) {
        redisCache.sadd(key, items);
    }

    /**
     * 向集合添加成员
     */
    public static <E> void sadd(String key, List<E> list) {
        redisCache.sadd(key, list);
    }

    /**
     * 返回集合中的一个随机元素
     * @param key
     */
    public static <T> T srandmember(String key) {
        return  redisCache.srandmember(key);
    }

    /**
     * 返回集合中的几个随机元素
     * @param key
     * @param count
     * @return
     */
    public static <T> List<T> srandmember(String key, int count) {
        return redisCache.srandmember(key, count);
    }

    /**
     *   命令判断成员元素是否是集合的成员
     * @param key
     * @param item
     * @return
     */
    public static boolean sismember(String key, Object item) {
        return redisCache.sismember(key, item);
    }


    /**
     * 返回集合中的所有成员
     * @param key
     * @return
     */
    public static <T> List<T> smembers(String key){
        return redisCache.smembers(key);
    }

    /**
     *  返回集合中元素的数量
     * @param key
     * @return
     */
    public static long scard(String key) {
        return redisCache.scard(key);
    }

    /**
     * 移除集合中一个或多个成员
     * @param key
     * @param items
     */
    public static void srem(String key, Object ... items) {
        redisCache.srem(key, items);
    }


    /**
     * 根据key pop 对象
     * @param key
     * @return
     */
    public static <T> T spop(String key){
        return redisCache.spop(key);
    }




    /** 有序集合 **/

    /**
     * 新增记录
     * @param key
     * @param item
     * @param score
     */
    public static void zadd(String key, Object item, double score) {
        redisCache.zadd(key, item, score);
    }

    /**
     *  通过分数返回有序集合指定区间内的成员
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    public static <T> List<T> zrangeByScore(String key, Double minScore, Double maxScore){
        return redisCache.zrangeByScore(key, minScore, maxScore);
    }

    /**
     *  返通过索引区间返回有序集合成指定区间内的成员
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static  <T> List<T> zrange(String key, Long start, Long end){
        return redisCache.zrange(key, start, end);
    }


    /**
     *  返回有序集中指定分数区间内的成员，分数从高到低排序
     * @param key
     * @param maxScore
     * @param minScore
     * @return
     */
    public static <T> List<T> zrevrangeByScore(String key, double maxScore, double minScore){
        return redisCache.zrevrangeByScore(key, maxScore, minScore);
    }

    /**
     * 返回有序集中指定区间内的成员，通过索引，分数从高到底
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static <T> List<T> zrevrange(String key, long start, long end){
       return redisCache.zrevrange(key, start, end);
    }

    /**
     * 移除有序集合中给定的分数区间的所有成员
     * @param key
     * @param minScore
     * @param maxScore
     */
    public static long zremrangeByScore(String key, double minScore, double maxScore) {
        return redisCache.zremrangeByScore(key, minScore, maxScore);

    }

    /**
     *   命令用于移除有序集合中给定的字典区间的所有成员。
     * @param key
     * @param min
     * @param max
     */
    public static long zremrangeByLex(String key, Object min, Object max) {
        return redisCache.zremrangeByLex(key, min, max);
    }

    /**
     *  移除有序集合中给定的排名区间的所有成员
     * @param key
     * @param start
     * @param end
     */
    public static long zremrangeByRank(String key, Long start, Long end) {
        return redisCache.zremrangeByRank(key, start, end);
    }

    /**
     *  移除有序集合中一个或多个成员
     */
    public static void zrem(String key, Object ... items) {
        redisCache.zrem(key, items);
    }

    /**
     *  返回有序集合中元素的数量
     * @param key
     * @return
     */
    public static long zcard(String key) {
        return redisCache.zcard(key);
    }

    /**
     * 返回有序集中指定成员的排名。其中有序集成员按分数值递增(从小到大)顺序排列
     * @param key
     * @param member
     * @return
     */
    public static long zrank(String key, Object member) {
        return redisCache.zrank(key, member);
    }

    /**
     * 返回有序集中，成员的分数值
     * @param key
     * @param member
     * @return
     */
    public static double zscore(String key, Object member) {
        return redisCache.zscore(key, member);
    }



    /**
     * 判断key 是否存在
     * @param key
     * @return
     */
    public static boolean exists(String key){
        return redisCache.exists(key);
    }

    /**
     * 转字符串
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        if(object instanceof String){
            return (String) object;
        }
        ObjectMapper objectMapper = new JacksonObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return "";
    }

    /**
     * 转出Map对象
     * @param object
     * @return
     */
    public static Map<String, String> toMap(Object object){
        Map<String, String> map = new HashMap<String, String>();
        Map<?, ?> objMap = new HashMap<Object, Object>();
        if(object instanceof Map){
            objMap = (Map<?, ?>)object;

        }
        else{
            if(!(object instanceof String)){
                objMap = parse(toJSONString(object), Map.class);
            }
            else{
                objMap = parse((String)object, Map.class);
            }
        }
        for(Map.Entry<?, ?> entry : objMap.entrySet()){
            Object obj = entry.getValue();
            String value = String.valueOf(entry.getValue());
            if(obj instanceof List) {
                value = toJSONString(obj);
            }
            map.put(String.valueOf(entry.getKey()), value);
        }

        return map;
    }

    /**
     * 转对象
     * @param value
     * @param clazz
     * @return
     * @throws IOException
     */
    public static <T> T parse(String value, Class<T> clazz){
        if(clazz == null || clazz.equals(String.class)){
            return (T) value;
        }
        ObjectMapper objectMapper = new JacksonObjectMapper();
        try {
            return objectMapper.readValue(value, clazz);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}