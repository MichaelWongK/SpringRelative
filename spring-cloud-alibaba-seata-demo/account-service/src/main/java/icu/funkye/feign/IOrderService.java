package icu.funkye.feign;

import icu.funkye.entity.Orders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author funkye
 * @date 2020/4/13
 */
@FeignClient(value = "order-service")
public interface IOrderService {

    @RequestMapping(value = "/save")
    Boolean save(@RequestBody Orders orders);

}
