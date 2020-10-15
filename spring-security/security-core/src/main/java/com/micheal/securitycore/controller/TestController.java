package com.micheal.securitycore.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/14 10:44
 * @Description
 */
@RestController
public class TestController {

    @PreAuthorize("hasRole('user')") // 只允许user角色访问
    @GetMapping("/getUser/{username}")
    public String getUser(@PathVariable("username") String username) {
        return username;
    }

}
