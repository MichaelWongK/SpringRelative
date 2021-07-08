package com.micheal.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2021/6/29 15:50
 * @Description kafka 消费者
 */
@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "micheal", groupId = "group_micheal")
    public void consume(String message) {
        log.info("### consume message: {}", message);
    }
}
