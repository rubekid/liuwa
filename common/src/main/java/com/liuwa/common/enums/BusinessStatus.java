package com.liuwa.common.enums;

/**
 * 操作状态
 * 
 * @author liuwa
 *
 */
public enum BusinessStatus
{
    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    FAIL(0);

    private int value;

    BusinessStatus(int  value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
