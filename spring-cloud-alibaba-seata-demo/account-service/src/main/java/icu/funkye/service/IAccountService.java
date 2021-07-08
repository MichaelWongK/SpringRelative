package icu.funkye.service;

import com.baomidou.mybatisplus.extension.service.IService;

import icu.funkye.entity.Account;

/**
 *
 * @author Funkye
 */
public interface IAccountService extends IService<Account> {

    void placeOrder(Integer userId, Integer productId, Integer count);
}
