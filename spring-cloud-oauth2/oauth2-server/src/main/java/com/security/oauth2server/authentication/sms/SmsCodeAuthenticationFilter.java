package com.security.oauth2server.authentication.sms;

import com.security.oauth2server.constants.SecurityConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/19 10:49
 * @Description 短信登录Filter
 * - 设置传输手机号的参数属性
 * - 构造方法调用父类的有参构造方法，主要用于设置其要拦截的url
 * - 照搬UsernamePasswordAuthenticationFilter 的 attemptAuthentication() 的实现 ，其内部需要改造有2点：1、 obtainMobile 获取 手机号信息 2、创建 SmsCodeAuthenticationToken 对象
 * - 为了实现短信登录也拥有记住我的功能，这里开放 setRememberMeServices() 方法用于设置 rememberMeServices 。
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    // 获取request中传递手机号的参数名
    private String mobileParameter = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;

    private boolean postOnly = true;

    // 构造函数，主要配置其拦截器要拦截的请求地址url
    public SmsCodeAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.DEFAULT_MOBILE_LOGIN_PROCESSING_URL, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // 判断请求是否为 POST 方式
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // 调用 obtainMobile 方法从request中获取手机号
        String mobile = obtainMobile(request);

        if (mobile == null) {
            mobile = "";
        }

        mobile = mobile.trim();

        // 创建 未认证的  SmsCodeAuthenticationToken  对象
        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile);
        setDetails(request, authRequest);
        // 调用认证方法
        return getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * 原封不动照搬UsernamePasswordAuthenticationFilter 的实现 （注意这里是 SmsCodeAuthenticationToken  ）
     */
    private void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * 获取手机号
     */
    private String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    /**
     * 开放设置 RemmemberMeServices 的set方法
     */
    @Override
    public void setRememberMeServices(RememberMeServices rememberMeServices) {
        super.setRememberMeServices(rememberMeServices);
    }
}
