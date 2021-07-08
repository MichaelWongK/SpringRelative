package com.micheal.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2021/6/29 16:26
 * @Description 订单实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    /**
     * 订单id
     */
    private long orderId;
    /**
     * 订单号
     */
    private String orderNum;
    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;

}
