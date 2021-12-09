package com.liuwa.framework.listener;

import com.liuwa.framework.observable.RedisKeyDeletionObservable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Observer;

/**
 *  redis key 过期监听
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 观察者
     */
    @Autowired
    private RedisKeyDeletionObservable observable;


    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 订阅
     */
    public void attach(Observer observer){
        observable.addObserver(observer);
    }

    /**
     * 取消订阅
     * @param observer
     */
    public void detach(Observer observer){
        observable.deleteObserver(observer);
    }

    /**
     * 监听消息
     * @param message
     * @param pattern
     */
    public void onMessage(Message message, byte[] pattern) {
        super.onMessage(message, pattern);
        String key = message.toString(); // 获取key
        logger.debug("redis key过期：{}", key);
        observable.notifyObservers(key);
    }
 
}