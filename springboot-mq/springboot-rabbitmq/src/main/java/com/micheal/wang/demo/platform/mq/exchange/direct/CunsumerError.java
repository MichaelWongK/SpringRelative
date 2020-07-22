package com.micheal.wang.demo.platform.mq.exchange.direct;

import com.micheal.wang.demo.platform.mq.exchange.util.MQConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeoutException;

public class CunsumerError {
    private final static String EXCHANGE_NAME = "my_exchange_direct";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接和channel
        Connection conn = MQConnectionUtils.connect();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        // 由RabbitMQ自行创建的临时队列,唯一且随消费者的中止而自动删除的队列
        String queueName = "consumer_error";
        // binding
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("error consumer 获取producer消息：" + msg);
            }
        };
        // 消费者监听队列消息
        channel.basicConsume(queueName, true, consumer);
    }
}
