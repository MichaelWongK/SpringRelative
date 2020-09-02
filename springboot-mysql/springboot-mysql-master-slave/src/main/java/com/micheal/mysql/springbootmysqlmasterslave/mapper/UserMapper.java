package com.micheal.mysql.springbootmysqlmasterslave.mapper;

import com.micheal.mysql.springbootmysqlmasterslave.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author micheal.wang
 * @since 2020-09-01
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

}
