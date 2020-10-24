package com.micheal.oauth2resource.controller;

import com.micheal.oauth2resource.domain.TbContent;
import com.micheal.oauth2resource.dto.ResponseResult;
import com.micheal.oauth2resource.service.TbContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/25 3:41
 * @Description
 */
@RestController
public class ContentController {

    @Autowired
    private TbContentService tbContentService;

    @RequestMapping("/")
    public ResponseResult<List<TbContent>> list() {
        return new ResponseResult(HttpStatus.OK.value(), HttpStatus.OK.toString(), tbContentService.list());
    }
}
