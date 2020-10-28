package com.security.oauth2server.properties;

import lombok.Data;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/19 15:45
 * @Description
 */
@Data
public class SmsProperties {

    // 需要发送验证码url，多个请求用 , 分割
    private String sendSmsUrl;
}
