package com.liuwa.common.enums;

/**
 * 用户状态
 *
 * @author liuwa
 */
public enum UserStatus
{
    ENABLE(1, "正常"), DISABLE(0, "停用");

    private final int code;
    private final String info;

    UserStatus(int code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public int getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
