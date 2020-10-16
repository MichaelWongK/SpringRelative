package com.micheal.securitycore.config;

import com.micheal.securitycore.handle.security.CustomAuthenticationFailureHandler;
import com.micheal.securitycore.handle.security.CustomAuthenticationSuccessHandler;
import com.micheal.securitycore.properties.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.annotation.Resource;

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

        http.authorizeRequests()
                .antMatchers("/index", "/", "loginRequire",
                        securityProperties.getLogin().getLoginPage(),
                        securityProperties.getLogin().getLoginSuccessUrl(),
                        securityProperties.getLogin().getLoginErrorUrl()).permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();

    }
}
