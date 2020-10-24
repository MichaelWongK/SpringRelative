package com.security.oauth2server.organization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.security.oauth2server.domain.TbPermission;

import java.util.List;

public interface TbPermissionMapper extends BaseMapper<TbPermission> {
    int deleteByPrimaryKey(Long id);

    int insert(TbPermission record);

    int insertSelective(TbPermission record);

    TbPermission selectByPrimaryKey(Long id);

    List<TbPermission> selectByUserId(Long userId);

    int updateByPrimaryKeySelective(TbPermission record);

    int updateByPrimaryKey(TbPermission record);
}