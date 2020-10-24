package com.micheal.oauth2resource.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micheal.oauth2resource.domain.TbContent;

public interface TbContentMapper extends BaseMapper<TbContent> {
    int deleteByPrimaryKey(Long id);

    int insert(TbContent record);

    int insertSelective(TbContent record);

    TbContent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TbContent record);

    int updateByPrimaryKey(TbContent record);
}