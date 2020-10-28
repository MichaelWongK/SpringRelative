package com.security.oauth2server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.security.oauth2server.domain.TbUser;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/25 2:00
 * @Description
 */
public interface TbUserService extends IService<TbUser> {

    TbUser getByUserName(String username);

    TbUser getByUserMobile(String mobile);
}
