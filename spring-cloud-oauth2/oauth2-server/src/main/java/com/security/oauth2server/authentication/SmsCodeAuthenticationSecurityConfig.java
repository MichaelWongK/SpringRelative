package com.security.oauth2server.authentication;

import com.security.oauth2server.authentication.sms.SmsCodeAuthenticationFilter;
import com.security.oauth2server.authentication.sms.SmsCodeAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/19 17:09
 * @Description
 */
@Configuration
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler ;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Resource
    private UserDetailsService userDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        // 设置 AuthenticationManager
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        // 分别设置成功和失败处理器
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        // 设置 RememberMeServices
        smsCodeAuthenticationFilter.setRememberMeServices(http.getSharedObject(RememberMeServices.class));

        // 创建 SmsCodeAuthenticationProvider 并设置 userDetailsService
        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

        // 将Provider添加到其中
        http.authenticationProvider(smsCodeAuthenticationProvider)
                // 将过滤器添加到UsernamePasswordAuthenticationFilter后面
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
