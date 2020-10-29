package com.security.oauth2server.authentication;

import com.security.oauth2server.authentication.handler.MyAuthenticationFailureHandler;
import com.security.oauth2server.authentication.handler.MyAuthenticationSucessHandler;
import com.security.oauth2server.properties.SecurityProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/16 10:39
 * @Description 表单登陆配置
 */
@Component
public class FormAuthenticationConfig {

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private MyAuthenticationSucessHandler myAuthenticationSucessHandler;

    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

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
    public void configure(HttpSecurity http) throws Exception {
//        http.formLogin()
//            .and()
//                .authorizeRequests()
//                .antMatchers("/index", "/").permitAll()
//                .anyRequest().authenticated();

        http.formLogin()
                //可以设置自定义的登录页面 或者 （登录）接口
                // 注意1： 一般来说设置成（登录）接口后，该接口会配置成无权限即可访问，所以会走匿名filter, 也就意味着不会走认证过程了，所以我们一般不直接设置成接口地址
                // 注意2： 这里配置的 地址一定要配置成无权限访问，否则将出现 一直重定向问题（因为无权限后又会重定向到这里配置的登录页url）
//                .loginPage(securityProperties.getLogin().getLoginPage())
                //.loginPage("/loginRequire")
                // 指定验证凭据的URL（默认为 /login） ,
                // 注意1：这里修改后的 url 会意味着  UsernamePasswordAuthenticationFilter 将 验证此处的 url
                // 注意2： 与 loginPage设置的接口地址是有 区别, 一但 loginPage 设置了的是访问接口url，那么此处配置将无任何意义
                // 注意3： 这里设置的 Url 是有默认无权限访问的
                .loginProcessingUrl(securityProperties.getLogin().getLoginUrl())
                //分别设置成功和失败的处理器
//                .successHandler(myAuthenticationSucessHandler)
                .failureHandler(myAuthenticationFailureHandler);

    }
}
