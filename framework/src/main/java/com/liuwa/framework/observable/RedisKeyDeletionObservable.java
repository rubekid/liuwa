package com.liuwa.framework.observable;

import org.springframework.stereotype.Component;

import java.util.Observable;

/**
 * redis key 删除/过期 观察
 */
@Component
public class RedisKeyDeletionObservable extends Observable {

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}
