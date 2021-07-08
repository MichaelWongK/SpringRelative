package icu.funkye.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import icu.funkye.entity.Account;
import icu.funkye.entity.Orders;
import icu.funkye.entity.Product;
import icu.funkye.mapper.AccountMapper;
import icu.funkye.service.IAccountService;
import icu.funkye.feign.IOrderService;
import icu.funkye.feign.IProductService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements IAccountService {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private IProductService productService;

    /**
     * 下单：创建订单、减库存，涉及到两个服务
     *
     * @param userId
     * @param productId
     * @param count
     */
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public void placeOrder(Integer userId, Integer productId, Integer count) {
        Product product = productService.getById(productId);
        BigDecimal orderMoney = new BigDecimal(count).multiply(product.getPrice());

        Orders order = new Orders()
                .setAccountId(userId)
                .setProductId(productId)
                .setAmount(orderMoney)
                .setSum(count);
        // 保存订单
        orderService.save(order);
        // 扣减库存
        productService.deduct(productId, count);

    }
}
