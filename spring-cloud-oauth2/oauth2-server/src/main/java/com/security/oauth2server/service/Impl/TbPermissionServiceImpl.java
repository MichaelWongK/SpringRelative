package com.security.oauth2server.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.security.oauth2server.domain.TbPermission;
import com.security.oauth2server.organization.mapper.TbPermissionMapper;
import com.security.oauth2server.service.TbPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/25 2:18
 * @Description
 */
@Service
public class TbPermissionServiceImpl extends ServiceImpl<TbPermissionMapper, TbPermission> implements TbPermissionService {

    @Override
    public List<TbPermission> selectByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
}
