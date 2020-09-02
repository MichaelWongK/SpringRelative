package com.micheal.mysql.springbootmysqlmasterslave.service;

import com.micheal.mysql.springbootmysqlmasterslave.annotation.Master;
import com.micheal.mysql.springbootmysqlmasterslave.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micheal.mysql.springbootmysqlmasterslave.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author micheal.wang
 * @since 2020-09-01
 */
public interface UserService extends IService<User> {

    public void insert(User user);

}
