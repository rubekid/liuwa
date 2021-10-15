package com.liuwa.common.utils;

import com.liuwa.common.core.redis.RedisCache;
import com.liuwa.common.utils.spring.SpringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具
 */
public class LockUtils {

    private static RedisCache redisCache = SpringUtils.getBean(RedisCache.class);

    /**
     * 尝试获取锁
     * @param lockKey
     * @param timeout
     * @param timeUnit
     * @return
     */
    public static boolean tryLock(String lockKey, int timeout, TimeUnit timeUnit){
        return redisCache.tryLock(lockKey, timeout, timeUnit);
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @return
     */
    public static boolean tryLock(String lockKey){
        return tryLock(lockKey, 10, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁(不过期)
     * @param lockKey
     * @return
     */
    public static boolean tryLockForever(String lockKey){
        return tryLock(lockKey, 0, TimeUnit.SECONDS);
    }

    /**
     * 同步锁
     * @param lockKey
     * @param waitTimeout
     * @return
     */
    public static boolean lock(String lockKey, Integer waitTimeout){
        return redisCache.lock(lockKey, waitTimeout);

    }

    /**
     * 同步锁
     * @param lockKey
     * @return
     */
    public static boolean lock(String lockKey){
        return lock(lockKey, null);
    }

    /**
     * 解除锁定
     * @param lockKey
     */
    public static void unlock(String lockKey){
        redisCache.unlock(lockKey);
    }


}
