package com.security.oauth2server.properties;

import lombok.Data;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/27 0:01
 * @Description
 */
@Data
public class TokenStoreProperties {

    private boolean jwtTokenStoreEnable;
    private boolean redisTokenStoreEnable;
}
