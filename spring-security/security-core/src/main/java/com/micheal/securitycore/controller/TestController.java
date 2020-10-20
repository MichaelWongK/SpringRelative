package com.micheal.securitycore.controller;

import com.micheal.securitycore.common.util.StringRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/14 10:44
 * @Description
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private StringRedisUtils stringRedisUtils;

    @PreAuthorize("hasRole('user')") // 只允许user角色访问
    @GetMapping("/getUser/{username}")
    public String getUser(@PathVariable("username") String username) {
        return username;
    }


    @GetMapping("/loginRequire")
    public String loginRequire() {
        return "自定义登陆接口，表示不走认证过程";
    }

    @GetMapping("/send/sms/{mobile}")
    public void sendSms(@PathVariable String mobile) {
        // 随机生成 6 位的数字串
        String code = RandomStringUtils.randomNumeric(6);
        // 通过 stringRedisTemplate 缓存到redis中
        stringRedisUtils.setEx(mobile, code, 60 * 5, TimeUnit.SECONDS);
        // 模拟发送短信验证码
        log.info("向手机： " + mobile + " 发送短信验证码是： " + code);
    }

}
