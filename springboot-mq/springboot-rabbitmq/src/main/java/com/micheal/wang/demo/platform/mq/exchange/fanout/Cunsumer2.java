package com.micheal.wang.demo.platform.mq.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeoutException;

public class Cunsumer2 {
    private final static String EXCHANGE_NAME = "exchange_fanout";
    private final static String HOST = "47.100.93.46";
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static Integer PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        // 获取不同的pid，方便标识不同的消费者
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        // 创建连接和channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setPort(PORT);
        factory.setVirtualHost("/");

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 由RabbitMQ自行创建的临时队列,唯一且随消费者的中止而自动删除的队列
        String queueName = channel.queueDeclare().getQueue();
        // binding
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println(pid + " 已经创建正在等待消息...");
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("consumer2获取producer消息：" + msg);
            }
        };
        // 消费者监听队列消息
        channel.basicConsume(queueName, consumer);
    }
}
