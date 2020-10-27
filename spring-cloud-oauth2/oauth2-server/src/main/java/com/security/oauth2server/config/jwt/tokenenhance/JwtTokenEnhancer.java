package com.security.oauth2server.config.jwt.tokenenhance;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/27 10:51
 * @Description jwt内容增强器
 */
public class JwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> tokenInfo = new HashMap<>();
        // 扩展返回的token信息
        tokenInfo.put("username", authentication.getName());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(tokenInfo);
        return accessToken;
    }
}
