package com.liuwa.common.core.redis;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 *
 * @author liuwa
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
public class RedisCache {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 等待超时毫秒数
     */
    private static int WAIT_TIMEOUT_MSECS = 60000;

    /**
     * 锁自动过期毫秒数
     */
    private static int LOCK_EXPIRE_MSECS = 60000;

    /**
     * 键名前缀
     */
    @Value("${spring.redis.prefix:}")
    private String keyPrefix;


    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 去掉前缀
     * @param key
     * @return
     */
    public String clearKeyPrefix(String key){
        if(key.startsWith(keyPrefix + ":")){
            return key.substring(keyPrefix.length() + 1);
        }
        return key;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout)
    {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取过期时间
     * @param key
     * @return
     */
    public long getExpire(Object key){
        return redisTemplate.getExpire(key);
    }

    /**
     * 移除过期设置
     * @param key
     * @return
     */
    public boolean persist(Object key)  {
        return redisTemplate.persist(key);
    }

    /**
     * 判断 可以是否存在
     * @param key
     * @return
     */
    public boolean exists(Object key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection)
    {
        return redisTemplate.delete(collection);
    }

    /**
     * 删除hash 字段
     * @param key
     * @param fields
     */
    public void hdel(Object key, Object ... fields){
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param value 待缓存的数据
     * @return 缓存的对象
     */
    public <T> long rpush(final String key, final T value)
    {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param value 待缓存的数据
     * @return 缓存的对象
     */
    public <T> long lpush(final String key, final T value)
    {
        Long count = redisTemplate.opsForList().leftPush(key, value);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet)
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern)
    {
        return redisTemplate.keys(pattern);
    }

    /**
     * 增加值
     * @param key
     * @param incrValue
     */
    public long  incrBy(String key, long incrValue){
        return redisTemplate.opsForValue().increment(key, incrValue);
    }

    /**
     * 增加值
     * @param key
     * @param incrValue
     */
    public double incrBy(String key, double incrValue){
        return redisTemplate.opsForValue().increment(key, incrValue);
    }

    /**
     * 减少值
     * @param key
     * @param incrValue
     */
    public long  decrBy(String key, long incrValue){
        return redisTemplate.opsForValue().decrement(key, incrValue);
    }





    /**
     * 哈希表字段自增
     * @param key
     * @param field
     * @param value
     */
    public long hincrBy(String key, String field, long value) {
        return redisTemplate.opsForHash().increment(key, field, value);
    }

    /**
     * 哈希表字段自增
     * @param key
     * @param field
     * @param value
     */
    public double hincrBy(String key, String field, double value) {
        return redisTemplate.opsForHash().increment(key, field, value);
    }


    /**
     * 删除
     * @param key
     */
    public boolean remove(String key){
        return redisTemplate.delete(key);
    }


    /**
     * 发布
     * @param channel
     * @param message
     */
    public void publish(String channel, Object message){
        redisTemplate.convertAndSend(channel, message);
        logger.info("\n================== 发布消息 ========================\n"
                + "{} {}"
                + "\n================== 发布消息结束 ========================", channel, JSONObject.toJSONString(message));
    }


    /**
     * 根据key pop 对象
     * @param key
     * @return
     */
    public <T> T pop(String key){
        return lpop(key);
    }

    /**
     * 根据key lpop 对象
     * @param key
     * @return
     */
    public <T> T lpop(String key){
        ListOperations<String, T> operations = redisTemplate.opsForList();
        return operations.leftPop(key);
    }

    /**
     * 根据key rpop 对象
     * @param key
     * @return
     */
    public <T> T rpop(String key){
        ListOperations<String, T> operations = redisTemplate.opsForList();
        return operations.rightPop(key);
    }


    /**
     * 根据key pop 对象
     * @param key
     * @param timeout 超时 （秒）
     * @return
     */
    public <T> T blpop(String key, int timeout){
        ListOperations<String, T> operations = redisTemplate.opsForList();
        return operations.leftPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 根据key pop 对象
     * @param key
     * @return
     */
    public <T> T blpop(String key){
        ListOperations<String, T> operations = redisTemplate.opsForList();
        return operations.leftPop(key, 0, TimeUnit.SECONDS);
    }

    /**
     * 移除列表中与参数 VALUE 相等的元素
     * @param key
     * @param value
     */
    public void lrem(String key, Object value) {
        lrem(key, 0, value);
    }

    /**
     * 移除列表中与参数 VALUE 相等的元素
     * @param key
     * @param count
     * @param value
     */
    public void lrem(String key, Integer count, Object value) {
        redisTemplate.opsForList().remove(key, count, value);
    }


    /**   集合   **/

    /**
     * 向集合添加成员
     */
    public void sadd(String key, Object ... items) {
        redisTemplate.opsForSet().add(key, items);
    }

    /**
     * 向集合添加成员
     */
    public <E> void sadd(String key, List<E> list) {
        redisTemplate.opsForSet().add(key, list);
    }

    /**
     * 返回集合中的一个随机元素
     * @param key
     */
    public <T> T srandmember(String key) {
        SetOperations<String, T> operations = redisTemplate.opsForSet();
        return operations.randomMember(key);
    }

    /**
     * 返回集合中的几个随机元素
     * @param key
     * @param count
     * @return
     */
    public <T> List<T> srandmember(String key, int count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     *   命令判断成员元素是否是集合的成员
     * @param key
     * @param item
     * @return
     */
    public boolean sismember(String key, Object item) {
        return redisTemplate.opsForSet().isMember(key, item);
    }


    /**
     * 返回集合中的所有成员
     * @param key
     * @return
     */
    public <T> List<T> smembers(String key){
        Set<T> set =  redisTemplate.opsForSet().members(key);
        List<T> list = new ArrayList<T>();
        for(T item : set){
            list.add(item);
        }
        return list;
    }

    /**
     *  返回集合中元素的数量
     * @param key
     * @return
     */
    public long scard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 移除集合中一个或多个成员
     * @param key
     * @param items
     */
    public void srem(String key, Object ... items) {
        redisTemplate.opsForSet().remove(key, items);
    }


    /**
     * 根据key pop 对象
     * @param key
     * @return
     */
    public <T> T spop(String key){
        SetOperations<String, T> operations = redisTemplate.opsForSet();
        return operations.pop(key);
    }




    /** 有序集合 **/

    /**
     * 新增记录
     * @param key
     * @param item
     * @param score
     */
    public void zadd(String key, Object item, double score) {
        redisTemplate.opsForZSet().add(key, item, score);
    }

    /**
     *  通过分数返回有序集合指定区间内的成员
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    public <T> List<T> zrangeByScore(String key, Double minScore, Double maxScore){
        Set<T> set =  redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
        List<T> list = new ArrayList<T>();
        for(T item : set){
            list.add(item);
        }
        return list;
    }

    /**
     *  返通过索引区间返回有序集合成指定区间内的成员
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> List<T> zrange(String key, Long start, Long end){
        Set<T> set =  redisTemplate.opsForZSet().range(key, start, end);
        List<T> list = new ArrayList<T>();
        for(T item : set){
            list.add(item);
        }
        return list;
    }


    /**
     *  返回有序集中指定分数区间内的成员，分数从高到低排序
     * @param key
     * @param maxScore
     * @param minScore
     * @return
     */
    public <T> List<T> zrevrangeByScore(String key, double maxScore, double minScore){
        Set<T> set =  redisTemplate.opsForZSet().reverseRangeByScore(key, minScore, maxScore);
        List<T> list = new ArrayList<T>();
        for(T item : set){
            list.add(item);
        }
        return list;
    }

    /**
     *  返回有序集中指定区间内的成员，通过索引，分数从高到底
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> List<T> zrevrange(String key, long start, long end){
        Set<T> set =  redisTemplate.opsForZSet().reverseRange(key, start, end);
        List<T> list = new ArrayList<T>();
        for(T item : set){
            list.add(item);
        }
        return list;
    }

    /**
     * 移除有序集合中给定的分数区间的所有成员
     * @param key
     * @param minScore
     * @param maxScore
     */
    public long zremrangeByScore(String key, double minScore, double maxScore) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, minScore, maxScore);

    }

    /**
     *   命令用于移除有序集合中给定的字典区间的所有成员。
     * @param key
     * @param min
     * @param max
     */
    public long zremrangeByLex(String key, Object min, Object max) {
        RedisZSetCommands.Range range = new RedisZSetCommands.Range();
        range.gte(min);
        range.lte(max);
        Set<Object> set = redisTemplate.opsForZSet().rangeByLex(key, range);
        return redisTemplate.opsForZSet().remove(key, set.toArray());
    }

    /**
     *  移除有序集合中给定的排名区间的所有成员
     * @param key
     * @param start
     * @param end
     */
    public long zremrangeByRank(String key, Long start, Long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     *  移除有序集合中一个或多个成员
     */
    public void zrem(String key, Object ... items) {
        redisTemplate.opsForZSet().remove(key, items);
    }

    /**
     *  返回有序集合中元素的数量
     * @param key
     * @return
     */
    public long zcard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 返回有序集中指定成员的排名。其中有序集成员按分数值递增(从小到大)顺序排列
     * @param key
     * @param member
     * @return
     */
    public long zrank(String key, Object member) {
        return redisTemplate.opsForZSet().rank(key, member);
    }

    /**
     * 返回有序集中，成员的分数值
     * @param key
     * @param member
     * @return
     */
    public double zscore(String key, Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }



    /**
     * 同步锁
     * @param lockKey
     * @param waitTimeout
     * @return
     */
    public boolean lock(String lockKey, Integer waitTimeout){

        if(waitTimeout == null){
            waitTimeout = WAIT_TIMEOUT_MSECS; // 60秒
        }
        while (waitTimeout >= 0) {
            long ttf = System.currentTimeMillis() + LOCK_EXPIRE_MSECS;

            // 获取锁
            if (redisTemplate.opsForValue().setIfAbsent(lockKey, ttf, LOCK_EXPIRE_MSECS, TimeUnit.MILLISECONDS)){
                return true;
            }

            waitTimeout -= 100;
            try {
                if(waitTimeout >= 0){
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return false;

    }

    /**
     * 同步锁
     * @param lockKey
     * @return
     */
    public boolean lock(String lockKey){
        return lock(lockKey, null);
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param timeout
     * @param timeUnit
     * @return
     */
    public boolean tryLock(String lockKey, int timeout, TimeUnit timeUnit){
        long timestamp = System.currentTimeMillis();

        if(timeout == 0){
            return redisTemplate.opsForValue().setIfAbsent(lockKey, timestamp);
        }
        // 获取锁
        else{
            return redisTemplate.opsForValue().setIfAbsent(lockKey, timestamp, timeout, timeUnit);
        }

    }

    /**
     * 解除锁定
     * @param lockKey
     */
    public void unlock(String lockKey){
        redisTemplate.delete(lockKey);
    }


}
