package com.micheal.mysql.springbootmysqlmasterslave.service.impl;

import com.micheal.mysql.springbootmysqlmasterslave.entity.User;
import com.micheal.mysql.springbootmysqlmasterslave.mapper.UserMapper;
import com.micheal.mysql.springbootmysqlmasterslave.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author micheal.wang
 * @since 2020-09-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void insert(User user) {
        this.save(user);
    }
}
