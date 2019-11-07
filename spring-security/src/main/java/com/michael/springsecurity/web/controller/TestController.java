package com.michael.springsecurity.web.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author michael.wang
 * @date 2019-11-06 09:43
 */
@RestController
public class TestController {
    @GetMapping("index")
    public Object index(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
