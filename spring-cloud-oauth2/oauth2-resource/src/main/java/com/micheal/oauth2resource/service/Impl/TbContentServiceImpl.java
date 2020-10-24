package com.micheal.oauth2resource.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micheal.oauth2resource.domain.TbContent;
import com.micheal.oauth2resource.platform.mapper.TbContentMapper;
import com.micheal.oauth2resource.service.TbContentService;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/25 3:39
 * @Description
 */
@Service
public class TbContentServiceImpl extends ServiceImpl<TbContentMapper, TbContent> implements TbContentService {
}
