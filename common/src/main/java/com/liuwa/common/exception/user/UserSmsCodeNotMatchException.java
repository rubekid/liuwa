package com.liuwa.common.exception.user;

/**
 * 用户短信验证码不正确或不符合规范异常类
 * 
 * @author liuwa
 */
public class UserSmsCodeNotMatchException extends UserException
{
    private static final long serialVersionUID = 1L;

    public UserSmsCodeNotMatchException()
    {
        super("user.sms.code.not.match", null);
    }
}
