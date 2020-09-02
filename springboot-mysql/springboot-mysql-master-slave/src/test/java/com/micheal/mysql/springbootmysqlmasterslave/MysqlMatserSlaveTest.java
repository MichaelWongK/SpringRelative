package com.micheal.mysql.springbootmysqlmasterslave;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.micheal.mysql.springbootmysqlmasterslave.entity.User;
import com.micheal.mysql.springbootmysqlmasterslave.service.BlogService;
import com.micheal.mysql.springbootmysqlmasterslave.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/9/2 10:32
 * @Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlMatserSlaveTest {

    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;

    @Test
    public void masterSlave() {
        userService.getOne(new QueryWrapper<User>().eq("username", "micheal.wang"));
    }


    @Test
    public void insert() {
        User user = new User();
        user.setUsername("abc");
        user.setPassword("abc");
        user.setEmail("ssa");
        userService.save(user);
    }

    @Test
    public void insert2() {
        User user = new User();
        user.setUsername("abc");
        user.setPassword("abc");
        user.setEmail("ssa");
        userService.insert(user);
    }

    @Test
    public void Test() {
        System.out.println("***********************");
        insert();
        System.out.println("***********************");
        masterSlave();
        System.out.println("***********************");
        insert2();

    }
}
