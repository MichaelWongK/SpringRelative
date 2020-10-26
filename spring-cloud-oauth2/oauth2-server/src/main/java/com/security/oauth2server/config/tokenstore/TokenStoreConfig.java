package com.security.oauth2server.config.tokenstore;

import com.security.oauth2server.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/26 17:59
 * @Description
 */
@Configuration
public class TokenStoreConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private DataSource dataSource;

    /**
     * 配置令牌存储方式：TokenStore -> JdbcTokenStore
     * 基于 JDBC 实现，令牌保存到数据
     * @return
     */
    @Bean
    @Primary
    public TokenStore tokenStore() {
        if (securityProperties.getTokenStore().isJwtTokenStoreEnable()) {
            return new JwkTokenStore(""); // 需要补充jwt认证方式
        } else if (securityProperties.getTokenStore().isRedisTokenStoreEnable()) {
            return new RedisTokenStore(redisConnectionFactory); // redis token 存储方式
        }
//        InMemoryTokenStore(); 不建议使用
        return new JdbcTokenStore(dataSource); // 默认jdbc
    }

}
