package com.security.oauth2server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.security.oauth2server.domain.TbPermission;

import java.util.List;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2020/10/25 2:17
 * @Description
 */
public interface TbPermissionService extends IService<TbPermission> {

    List<TbPermission> selectByUserId(Long userId);
}
