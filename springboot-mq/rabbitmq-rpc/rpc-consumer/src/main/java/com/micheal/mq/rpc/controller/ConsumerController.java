package com.micheal.mq.rpc.controller;

import com.micheal.mq.common.QueueConstants;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 消息消费端
 * @公众号 全栈在路上
 * @GitHub https://github.com/liuyongfei1
 * @author lyf
 * @date 2020-05-17 18:00
 */
@Component
public class ConsumerController {

    @RabbitListener(queues = {QueueConstants.RPC_QUEUE1})
    public void handler(Message message, Channel channel, @Headers Map<String, Object> headers) throws IOException {
        System.out.println("收到消息：" + message.toString());

        Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {
            // 手动确认消息已消费
            channel.basicAck(tag, false);
        } catch (IOException e) {
            // 把消费失败的消息重新放入到队列, 以后可以继续消费
            channel.basicNack(tag, false, true);
            e.printStackTrace();
        }
    }
}
