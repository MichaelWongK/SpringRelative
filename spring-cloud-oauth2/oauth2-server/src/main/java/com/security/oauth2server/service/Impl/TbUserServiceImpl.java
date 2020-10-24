package com.security.oauth2server.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.security.oauth2server.domain.TbUser;
import com.security.oauth2server.organization.mapper.TbUserMapper;
import com.security.oauth2server.service.TbUserService;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/25 2:00
 * @Description
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    @Override
    public TbUser getByUserName(String username) {
        return getOne(new QueryWrapper<TbUser>().eq("username", username));
    }
}
