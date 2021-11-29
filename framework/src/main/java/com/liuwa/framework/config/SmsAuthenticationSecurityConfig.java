package com.liuwa.framework.config;

import com.liuwa.framework.security.result.CustomAuthenticationFailureHandler;
import com.liuwa.framework.security.result.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.SmsAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.SmsCodeLoginService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.SmsAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 短信验证码登录鉴权配置
 */
@Component
public class SmsAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SmsCodeLoginService smsCodeLoginService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsAuthenticationFilter smsCodeAuthenticationFilter = new SmsAuthenticationFilter();
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);

        SmsAuthenticationProvider smsCodeAuthenticationProvider = new SmsAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);
        smsCodeAuthenticationProvider.setSmsCodeService(smsCodeLoginService);

        http.authenticationProvider(smsCodeAuthenticationProvider);
        //.addFilterBefore(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // 加过滤自动校验
    }
}
