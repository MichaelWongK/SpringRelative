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

/**
 * 消息生产端
 * @author micheal.wang
 * @date 2020-01-25 18:30
 */
@Slf4j
@RestController
public class ProducerController {

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
    public String sendDirectMessage() {
        // 报文体
        String body = "=======报文的内容=====嘿嘿嘿======";
        // 封装Message
        Message message = converter(body);
        log.info(">>>>客户端>>> {}", message.toString());

        //使用sendAndReceive方法完成rpc调用
        Message receiveMessage = rabbitTemplate.sendAndReceive(QueueConstants.RPC_EXCHANGE, QueueConstants.RPC_QUEUE1, message);

        //提取rpc回应内容body
        String response = new String(receiveMessage.getBody());
        log.info(">>>>客户端接收返回结果>>> {}", response);
        log.info("rpc完成---------------------------------------------");
        return "rpc完成---------------------------------------------";
    }

    private Message converter(String body) {
        MessageProperties mp = new MessageProperties();
        byte[] src = body.getBytes(Charset.forName("UTF-8"));
        //mp.setReplyTo("micheal");    //加载AmqpTemplate时设置，这里设置没用
//        mp.setCorrelationId("2222");   //系统生成，这里设置没用
        mp.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        mp.setContentEncoding("UTF-8");
        mp.setContentLength((long) body.length());
        return new Message(src, mp);
    }

}
