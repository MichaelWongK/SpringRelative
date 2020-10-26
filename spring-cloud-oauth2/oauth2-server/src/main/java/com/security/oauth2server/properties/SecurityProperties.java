package com.security.oauth2server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/16 10:06
 * @Description 配置获取类
 */
@Configuration
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {


    private TokenStoreProperties tokenStore = new TokenStoreProperties();

}

