package com.micheal.securitycore.properties;

import lombok.Data;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/16 10:08
 * @Description
 */
@Data
public class LoginProperties {

    /**
     * 登陆页面
     */
    private String loginPage;
    /**
     * 登陆成功跳转url
     * 注意：这里loginSuccessUrl不能为 null 或者 空字符串
     * 因为我们在 secutiry 的 antMatcher设置无权限访问配置时获取了这个属性值
     * 没有默认值系统会报错
     */
    private String loginSuccessUrl;
    /**
     * 登陆失败跳转url
     */
    private String loginErrorUrl;

    /**
     * 登陆接口，即UsernamePasswordAuthenticationFilter需要匹配的地址(security默认是/login)
     */
    private String loginUrl;
    /**
     * 记住我有效时间，默认3600（ms）
     */
    private int rememberMeSeconds;
}
