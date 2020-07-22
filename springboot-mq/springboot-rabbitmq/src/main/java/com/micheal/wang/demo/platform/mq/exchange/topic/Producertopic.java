package com.micheal.wang.demo.platform.mq.exchange.topic;

import com.micheal.wang.demo.platform.mq.exchange.util.MQConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producertopic {

    private final static String EXCHANGE_NAME = "my_exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接
        Connection conn = MQConnectionUtils.connect();
        // 创建通道
        Channel channel = conn.createChannel();
        // 绑定的交换机 参数1交互机名称 参数2 exchange类型
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String routingKey = "log.info";
        String msg = "topic_msg_exchange: " + routingKey;
        // 将消息发送至exchange
        // 第二个参数为空类似于表示全局广播，只要绑定到该队列上的消费者理论上是都可以收到的。
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
        System.out.println("[send]：" + msg);
        // 关闭通道、连接
        channel.close();
        conn.close();
        // 注意：如果消费没有绑定交换机和队列，则消息会丢失
    }

}
