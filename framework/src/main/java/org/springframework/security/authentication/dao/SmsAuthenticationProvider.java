package org.springframework.security.authentication.dao;

import com.liuwa.common.utils.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.SmsCodeLoginService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SmsAuthenticationToken;

/**
 * 短信登陆鉴权 Provider
 */
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private SmsCodeLoginService smsCodeService;

    public SmsAuthenticationProvider() {
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken authenticationToken = (SmsAuthenticationToken) authentication;
        String username = (String) authenticationToken.getPrincipal();
        if(StringUtils.isEmpty(username)){
            throw new BadCredentialsException("用户名不能为空");
        }
        if(!smsCodeService.verify(authenticationToken)){
            throw new BadCredentialsException("验证码错误");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        SmsAuthenticationToken authenticationResult = new SmsAuthenticationToken(userDetails, userDetails.getAuthorities());
        authenticationResult.setDetails(authenticationResult.getDetails());
        return authenticationResult;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 SmsCodeAuthenticationToken 的子类或子接口
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }



    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    public void setSmsCodeService(SmsCodeLoginService smsCodeService){
        this.smsCodeService = smsCodeService;
    }

    public SmsCodeLoginService getSmsCodeService() {
        return smsCodeService;
    }
}