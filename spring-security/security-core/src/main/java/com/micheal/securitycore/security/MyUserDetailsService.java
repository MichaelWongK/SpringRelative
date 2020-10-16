package com.micheal.securitycore.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/12 17:34
 * @Description
 * 自定义MyUserDetailsUserService类，实现 UserDetailsService 接口的 loadUserByUsername()方法，
 * 这里就简单的返回一个Spring Security 提供的 User 对象
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

    /**
     * PasswordEncoderFactories.createDelegatingPasswordEncoder()
     * 这里的 idForEncode 就是密码编码器的简略名称，可以通过
     * PasswordEncoderFactories.createDelegatingPasswordEncoder()
     * 内部实现看到默认是使用的前缀是 bcrypt 也就是 BCryptPasswordEncoder
     *
     *
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // 不能直接使用 创建 BCryptPasswordEncoder 对象来加密， 这种加密方式 没有 {bcrypt}  前缀，
        // 会导致在  matches 时导致获取不到加密的算法出现
        // java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"  问题
        // 问题原因是 Spring Security5 使用 DelegatingPasswordEncoder(委托)  替代 NoOpPasswordEncoder，
        // 并且 默认使用  BCryptPasswordEncoder 加密（注意 DelegatingPasswordEncoder 委托加密方法BCryptPasswordEncoder  加密前  添加了加密类型的前缀）  https://blog.csdn.net/alinyua/article/details/80219500
        // 注意Spring Security 5 开始没有使用 NoOpPasswordEncoder作为其默认的密码编码器，而是默认使用 DelegatingPasswordEncoder 作为其密码编码器，其 encode 方法是通过 密码编码器的名称作为前缀 + 委托各类密码编码器来实现encode的。
        return new User("micheal",  PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456"), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

}
