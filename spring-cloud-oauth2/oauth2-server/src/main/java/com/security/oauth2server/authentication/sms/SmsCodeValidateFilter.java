package com.security.oauth2server.authentication.sms;

import com.security.oauth2server.authentication.service.RedisCodeService;
import com.security.oauth2server.constants.SecurityConstants;
import com.security.oauth2server.exception.ValidateCodeException;
import com.security.oauth2server.properties.SecurityProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/19 15:09
 * @Description
 */
@Component
public class SmsCodeValidateFilter extends OncePerRequestFilter implements InitializingBean {

    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Resource
    private RedisCodeService redisCodeService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        if (StringUtils.equalsIgnoreCase(SecurityConstants.DEFAULT_MOBILE_LOGIN_PROCESSING_URL, request.getRequestURI())
                && StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            try {
                validateCode(new ServletWebRequest(request));
            } catch (Exception e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException(e.getMessage()));
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void validateCode(ServletWebRequest servletWebRequest) throws Exception {
        String smsCodeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "smsCode");
        String mobileInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "mobile");

        String codeInRedis = redisCodeService.get(servletWebRequest, mobileInRequest);

        if (StringUtils.isBlank(smsCodeInRequest)) {
            throw new ValidateCodeException("验证码不能为空！");
        }
        if (codeInRedis == null) {
            throw new ValidateCodeException("验证码已过期！");
        }
        if (!StringUtils.equalsIgnoreCase(codeInRedis, smsCodeInRequest)) {
            throw new ValidateCodeException("验证码不正确！");
        }
        redisCodeService.remove(servletWebRequest, mobileInRequest);

    }


}
