package com.security.oauth2server.authentication.sms;

import com.security.oauth2server.constants.SecurityConstants;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/19 11:19
 * @Description
 */
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;


    private final Object principal;

    private final String type = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;


    /**
     * 未认证时，内容为手机号
     * @param mobile
     */
    public SmsCodeAuthenticationToken(String mobile) {
        super(null);
        this.principal = mobile;
        this.setAuthenticated(false);
    }

    /**
     *
     * 认证成功后，其中为用户信息
     *
     * @param principal
     * @param authorities
     */
    public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }



    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(isAuthenticated);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
