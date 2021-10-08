package com.liuwa.common.exception;

/**
 * 简易异常
 * 
 * @author liuwa
 */
public class SimpleException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public SimpleException(Throwable e)
    {
        super(e.getMessage(), e);
    }

    public SimpleException(String message)
    {
        super(message);
    }

    public SimpleException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
