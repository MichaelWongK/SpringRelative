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
     */
    private String loginSuccessUrl;
    /**
     * 登陆失败跳转url
     */
    private String loginErrorUrl;

}
