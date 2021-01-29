package com.micheal.mq.rpc.controller;

import com.micheal.mq.common.QueueConstants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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
public class RPCServer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {QueueConstants.RPC_QUEUE1})
    public void process(Message message, Channel channel, @Headers Map<String, Object> headers) throws IOException {
        log.info("Server收到发送的消息：{}", message.toString());

        // 模拟处理业务逻辑
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String msgBody = new String(message.getBody());
        String newMessage = msgBody + "，sleep 2000 ms。";
        Message respMsg = converter(newMessage, message.getMessageProperties().getCorrelationId());
        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getCorrelationId());
//        rabbitTemplate.send(QueueConstants.RPC_EXCHANGE, QueueConstants.RPC_QUEUE2, respMsg, correlationData);
        rabbitTemplate.convertAndSend(QueueConstants.RPC_EXCHANGE, QueueConstants.RPC_QUEUE2, respMsg, correlationData);
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
