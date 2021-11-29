package org.springframework.security.core.userdetails;

import org.springframework.security.web.authentication.SmsAuthenticationToken;

/**
 * 短信验证码服务
 */
public interface SmsCodeLoginService {

    /**
     * 短信验证码校验
     * @param authenticationToken
     * @return
     */
    boolean verify(SmsAuthenticationToken authenticationToken);
}