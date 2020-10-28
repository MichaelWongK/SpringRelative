package com.security.oauth2server.config;

import com.security.oauth2server.authentication.FormAuthenticationConfig;
import com.security.oauth2server.authentication.SmsCodeAuthenticationSecurityConfig;
import com.security.oauth2server.authentication.sms.SmsCodeValidateFilter;
import com.security.oauth2server.config.service.UserDetailsServiceImpl;
import com.security.oauth2server.constants.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * @Despcription: SpringSecurity配置
 * @author micheal.wang <a href="michael.won007@gmail.com"/>
 * @create 2020-02-12
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * 设置默认加密方式
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Autowired
    private FormAuthenticationConfig formAuthenticationConfig;
    @Autowired
    private SmsCodeValidateFilter smsCodeValidateFilter;
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    /**
     * 密码模式必须配置用户认证
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * @formLogin()  使用表单登录（默认请求地址为 /login）,在Spring Security 5 里其实已经将旧版本默认的  httpBasic()
     * 更换成 formLogin() 了，这里为了表明表单登录还是配置了一次。
     * @authorizeRequests() 开始请求权限配置
     * @antMatchers() 使用Ant风格的路径匹配，这里配置匹配 / 和 /index
     * @permitAll() 用户可任意访问
     * @anyRequest() 匹配所有路径
     * @authenticated() 用户登录后可访问
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin()
//            .and()
//                .authorizeRequests()
//                .antMatchers("/index", "/").permitAll()
//                .anyRequest().authenticated();

        formAuthenticationConfig.configure(http);

//        http.authorizeRequests()
        http.addFilterBefore(smsCodeValidateFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .apply(smsCodeAuthenticationSecurityConfig)
                .and()
                .authorizeRequests()
                .antMatchers(SecurityConstants.DEFAULT_PAGE_URL,
                        SecurityConstants.DEFAULT_LOGIN_PAGE_URL,
                        "/code/sms").permitAll()
                .anyRequest().authenticated()
                .and()
                // 开启记住我功能，意味着RememberMeAuthenticationFilter 将会从cookie中获取 token 信息
                .rememberMe()
                .and()
                .csrf().disable(); // 关闭csrf 跨站（域）攻击防控

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义认证与授权
        auth.userDetailsService(userDetailsService());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/oauth/check_token");
    }
}
