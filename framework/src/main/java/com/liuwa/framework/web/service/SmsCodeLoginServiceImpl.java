package com.liuwa.framework.web.service;

import com.liuwa.framework.manager.CaptchaManager;
import org.springframework.security.core.userdetails.SmsCodeLoginService;
import org.springframework.security.web.authentication.SmsAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * 短信验证处理
 *
 * @author liuwa
 */
@Service
public class SmsCodeLoginServiceImpl implements SmsCodeLoginService {

    @Override
    public boolean verify(SmsAuthenticationToken authenticationToken) {
        String phone = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();
        return CaptchaManager.simpleVerify(CaptchaManager.TYPE_LOGIN, phone, code);
    }
}
