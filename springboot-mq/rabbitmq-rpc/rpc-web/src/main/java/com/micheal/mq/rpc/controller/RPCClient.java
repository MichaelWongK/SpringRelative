package com.micheal.mq.rpc.controller;

import com.micheal.mq.common.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.UUID;

/**
 * 消息生产端
 * @author micheal.wang
 * @date 2020-01-25 18:30
 */
@Slf4j
@RestController
public class RPCClient {

    /**
     * RabbitTemplate提供了发送/接收消息的方法
     */
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 生产消息（RPC客户端）
     * @return
     */
    @GetMapping("/sendMessage")
    public String sendDirectMessage(String message) {
        // 报文体
        String body = "=======报文的内容=====嘿嘿嘿======";
        // 封装Message
        Message newMessage = converter(message);
        log.info(">>>>客户端发送消息>>> {}", newMessage.toString());

        //使用sendAndReceive方法完成rpc调用
        // 备注：使用sendAndReceive 这个方法发送消息时，消息的correlationId会变成系统动编制的 1,2,3 这种格式,因此通过手动set的方式没有用
        Message result = rabbitTemplate.sendAndReceive(QueueConstants.RPC_EXCHANGE, QueueConstants.RPC_QUEUE1, newMessage);


        String response = "";
        if (result != null) {
            // 获取已发送的消息的唯一消息id
            String correlationId = newMessage.getMessageProperties().getCorrelationId();

            // 提取RPC回应内容的header
            HashMap<String, Object> headers = (HashMap<String, Object>) result.getMessageProperties().getHeaders();

            // 获取RPC回应消息的消息id（备注：rabbitmq的配置参数里面必须开启spring.rabbitmq.publisher-confirms=true，否则headers里没有该项）
            String msgId = (String) headers.get("spring_returned_message_correlation");

            // 客户端从回调队列获取消息，匹配与发送消息correlationId相同的消息为应答结果
            if (msgId.equals(correlationId)) {
                // 提取RPC回应内容body
                response = new String(result.getBody());
                log.info(">>>>客户端收到RPCServer返回的消息为：" + response);
            }
        }

        return response;
    }

    /**
     * 将发送消息封装成Message
     *
     * @param message
     **/
    private Message converter(String message) {
        MessageProperties mp = new MessageProperties();
        byte[] src = message.getBytes(Charset.forName("UTF-8"));
        // 注意：由于在发送消息的时候，系统会自动生成消息唯一id，因此在这里手动设置的方式是无效的
        // CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        // mp.setCorrelationId("123456");
        mp.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        mp.setContentEncoding("UTF-8");
        mp.setContentLength((long) message.length());
        return new Message(src, mp);
    }

}
