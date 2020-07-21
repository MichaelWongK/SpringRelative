package com.micheal.wang.demo.platform.mq.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Producer {

    private final static String EXCHANGE_NAME = "exchange_fanout";
    private final static String HOST = "47.100.93.46";
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static Integer PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setPort(PORT);
        factory.setVirtualHost("/");

        // 创建连接
        Connection conn = factory.newConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        // 声明该channel是fanout类型
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        Date now = new Date();
        String msg = now.getTime() + "have log ...";
        // 将消息发送至exchange
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());
        System.out.println(now + " 已经生成了一条日志...\n" + "msg:" + msg);
        channel.close();
        conn.close();

    }

}
