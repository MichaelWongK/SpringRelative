package com.micheal.securitycore.config.security;

import com.micheal.securitycore.authentication.FormAuthenticationConfig;
import com.micheal.securitycore.authentication.SmsCodeAuthenticationSecurityConfig;
import com.micheal.securitycore.authentication.sms.ValidateCodeFilter;
import com.micheal.securitycore.constants.SecurityConstants;
import com.micheal.securitycore.properties.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/14 10:28
 * @Description Spring Security配置
 * @EnableWebSecurity 查看其注解源码，主要是引用WebSecurityConfiguration.class 和 加入了@EnableGlobalAuthentication 注解 ，
 * 这里就不介绍了，我们只要明白添加 @EnableWebSecurity 注解将开启 Security 功能。
 */
@EnableWebSecurity
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private FormAuthenticationConfig formAuthenticationConfig;

    @Resource
    private DataSource dataSource;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Resource
    private ValidateCodeFilter validateCodeFilter;

    @Bean
    protected PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
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
        http.addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .apply(smsCodeAuthenticationSecurityConfig)
                .and()
                .authorizeRequests()
                .antMatchers(SecurityConstants.DEFAULT_PAGE_URL,
                        SecurityConstants.DEFAULT_LOGIN_PAGE_URL,
                        "/send/sms/*",
                        securityProperties.getLogin().getLoginErrorUrl()).permitAll()
                .anyRequest().authenticated()
                .and()
                // 开启记住我功能，意味着RememberMeAuthenticationFilter 将会从cookie中获取 token 信息
                .rememberMe()
                // 设置 tokenRepository 默认使用 JdbcTokenRepositoryImpl ,意味着将从数据空中读取 token所代表的用户信息
                .tokenRepository(persistentTokenRepository())
                // 设置 userDetailsService , 和认证过程一样， RememberMe 有专门的RememberMeAuthenticationProvider ，
                // 意味着需要使用 UserDetailsService 加载 UserDetails 信息
                .userDetailsService(userDetailsService)
                // 设置 rememberMe 有效时间，通过配置来实现
                .tokenValiditySeconds(securityProperties.getLogin().getRememberMeSeconds())
                .and()
                .csrf().disable(); // 关闭csrf 跨站（域）攻击防控

    }
}
