package icu.funkye.controller;

import icu.funkye.service.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author funkye
 */
@RestController
public class TestController {

    private final static Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    IAccountService accountService;


    /**
     * 下单：插入订单表、扣减库存，模拟回滚
     *
     * @return
     */
    @RequestMapping("/placeOrder/commit")
    public Boolean placeOrderCommit() {

        accountService.placeOrder(1, 1, 2);
        return true;
    }

    /**
     * 下单：插入订单表、扣减库存，模拟回滚
     *
     * @return
     */
    @RequestMapping("/placeOrder/rollback")
    public Boolean placeOrderRollback() {
        // product-222 扣库存时模拟了一个业务异常,
        accountService.placeOrder(1, 222, 2);
        return true;
    }

}
