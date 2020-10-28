package com.security.oauth2server.config.service;

import com.security.oauth2server.domain.TbPermission;
import com.security.oauth2server.domain.TbUser;
import com.security.oauth2server.service.TbPermissionService;
import com.security.oauth2server.service.TbUserService;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/25 2:27
 * @Description 自定义用户认证与授权
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private TbPermissionService tbPermissionService;
    @Autowired
    private TbUserService tbUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        TbUser tbUser = tbUserService.getByUserName(username);
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
        if (tbUser != null) {
            // 获取用户授权
            List<TbPermission> tbPermissions = tbPermissionService.selectByUserId(tbUser.getId());

            // 声明用户授权
            tbPermissions.forEach(tbPermission -> {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(tbPermission.getEnname());
                grantedAuthorities.add(grantedAuthority);
            });
        }

        // 由框架完成认证工作
        return new User(username, tbUser.getPassword(), grantedAuthorities);
    }

    public UserDetails loadUserByUserMobile(String mobile) {
        TbUser tbUser = tbUserService.getByUserMobile(mobile);
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
        if (tbUser != null) {
            List<TbPermission> tbPermissions = tbPermissionService.selectByUserId(tbUser.getId());

            tbPermissions.forEach(tbPermission -> grantedAuthorities.add(new SimpleGrantedAuthority(tbPermission.getEnname())));
        }


        return new User(tbUser.getUsername(), tbUser.getPassword(), grantedAuthorities);
    }
}
