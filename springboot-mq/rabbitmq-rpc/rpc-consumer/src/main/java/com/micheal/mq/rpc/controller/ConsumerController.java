package com.micheal.mq.rpc.controller;

import com.micheal.mq.common.QueueConstants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 消息消费端
 * @公众号 全栈在路上
 * @GitHub https://github.com/liuyongfei1
 * @author lyf
 * @date 2020-05-17 18:00
 */
@Slf4j
@Component
public class ConsumerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {QueueConstants.RPC_QUEUE1})
    public void handler(Message message, @Headers Map<String, Object> headers) throws IOException {
        log.info("收到队列1消息：{}", message.toString());

        String msgBody = new String(message.getBody());
        Message respMsg = converter(msgBody + "返回了", message.getMessageProperties().getCorrelationId());
        rabbitTemplate.send(QueueConstants.RPC_EXCHANGE, QueueConstants.RPC_QUEUE2, respMsg);
    }

//    @RabbitListener(queues = {QueueConstants.RPC_QUEUE2})
//    public void receiveTopic2(Message msg, Channel channel) {
//        log.info("队列2:"+msg.toString());
//
//    }

    private Message converter(String s, String correlationId) {
        MessageProperties mp = new MessageProperties();
        byte[] src = s.getBytes(Charset.forName("UTF-8"));
        mp.setContentType("application/json");
        mp.setContentEncoding("UTF-8");
        mp.setCorrelationId(correlationId);

        return new Message(src, mp);
    }
}
