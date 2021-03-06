package com.micheal.wang.demo.platform.mq.exchange.topic;

import com.micheal.wang.demo.platform.mq.exchange.util.MQConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerLogXTopic {
    private final static String EXCHANGE_NAME = "my_exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接和channel
        Connection conn = MQConnectionUtils.connect();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        // 由RabbitMQ自行创建的临时队列,唯一且随消费者的中止而自动删除的队列
        String queueName = "topic_consumer_info";
        // 消费者绑定交换机 参数1 队列 参数2交换机 参数3 routingKey
        channel.queueBind(queueName, EXCHANGE_NAME, "log.*");
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("X 消费者获取生产者消息：" + msg);
            }
        };
        // 消费者监听队列消息
        channel.basicConsume(queueName, true, consumer);
    }
}
