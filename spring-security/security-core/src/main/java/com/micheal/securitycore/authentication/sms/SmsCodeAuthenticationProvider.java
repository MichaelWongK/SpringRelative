package com.micheal.securitycore.authentication.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/19 14:53
 * @Description sms 认证委托实现
 * 通过直接继承 AuthenticationProvider实现其接口方法 authenticate() 和 supports() 。
 * supports() 我们直接参照其他Provider写的，这个主要是判断当前处理的Authentication
 * 是否为SmsCodeAuthenticationToken或其子类。
 * authenticate() 我们就直接调用 userDetailsService的loadUserByUsername()方法简单实现，
 * 因为验证码已经在  ValidateCodeFilter 验证通过了，
 * 所以这里我们只要能通过手机号查询到用户信息那就直接判顶当前用户认证成功，
 * 并且生成 已认证 的 SmsCodeAuthenticationToken返回。
 *
 */
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        UserDetails user = userDetailsService.loadUserByUsername((String) authentication.getPrincipal());

        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authentication.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
