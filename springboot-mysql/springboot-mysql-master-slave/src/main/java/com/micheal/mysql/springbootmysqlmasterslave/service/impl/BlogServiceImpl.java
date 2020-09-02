package com.micheal.mysql.springbootmysqlmasterslave.service.impl;

import com.micheal.mysql.springbootmysqlmasterslave.entity.Blog;
import com.micheal.mysql.springbootmysqlmasterslave.mapper.BlogMapper;
import com.micheal.mysql.springbootmysqlmasterslave.mapper.UserMapper;
import com.micheal.mysql.springbootmysqlmasterslave.service.BlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private UserMapper userMapper;
}
