package com.liuwa.common.exception;

/**
 * 业务异常
 * 
 * @author liuwa
 */
public final class ExistException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ExistException()
    {
    }

    public ExistException(String message)
    {
        this.message = message;
    }

    public ExistException(String message, Integer code)
    {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage()
    {
        return detailMessage;
    }

    public String getMessage()
    {
        return message;
    }

    public Integer getCode()
    {
        return code;
    }

    public ExistException setMessage(String message)
    {
        this.message = message;
        return this;
    }

    public ExistException setDetailMessage(String detailMessage)
    {
        this.detailMessage = detailMessage;
        return this;
    }
}