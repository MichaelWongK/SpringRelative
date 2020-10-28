package com.security.oauth2server.constants;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/16 15:24
 * @Description
 */
public interface SecurityConstants {

    /**
     * 默认登陆页面
     */
    final String DEFAULT_LOGIN_PAGE_URL = "/loginUp.html";

    /**
     * 默认系统首页
     */
    final String DEFAULT_PAGE_URL = "/";

    /**
     * 默认登录请求
     */
    final String DEFAULT_LOGIN_PROCESSING_URL_FORM = "/loginUp";

    /**
     * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
     */
    String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";

    /**
     * 验证短信验证码时，http请求中默认的携带短信验证码信息的参数的名称
     */
    String DEFAULT_PARAMETER_NAME_CODE_SMS = "smsCode";

    /**
     * 默认的手机验证码登录请求处理url
     */
    String DEFAULT_MOBILE_LOGIN_PROCESSING_URL = "/login/mobile";
}
