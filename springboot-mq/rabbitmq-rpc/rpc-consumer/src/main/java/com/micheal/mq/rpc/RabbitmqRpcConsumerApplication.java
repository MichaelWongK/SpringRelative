package com.micheal.mq.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 消息消费端启动入口
 * @author micheal.wang
 * @date 2021-01-26 18:00
 */
@SpringBootApplication
public class RabbitmqRpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqRpcConsumerApplication.class, args);
    }

}
