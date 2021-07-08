package com.micheal.kafka.provider;

import com.alibaba.fastjson.JSONObject;
import com.micheal.kafka.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2021/6/29 15:51
 * @Description 消息生产者
 *
 */
@Slf4j
@Component
public class KafkaProvider {

    /**
     * 消息 TOPIC
     */
    private static final String TOPIC = "micheal";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(long orderId, String orderNum, LocalDateTime createTime) {
        // 构建订单类
        Order build = Order.builder()
                .orderId(orderId)
                .orderNum(orderNum)
                .createTime(createTime)
                .build();

        // 发送消息，订单类的 json 作为消息体
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, JSONObject.toJSONString(build));

        // 监听回调
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.info("### send message fail ...");
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {
                log.info("### send message success ...");
            }
        });
    }
}
