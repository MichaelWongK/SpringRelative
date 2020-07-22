package com.micheal.wang.demo.platform.mq.exchange.work;

import com.micheal.wang.demo.platform.mq.exchange.util.MQConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1 {
    private final static String QUEUENAME = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接和channel
        Connection conn = MQConnectionUtils.connect();
        Channel channel = conn.createChannel();
        channel.queueDeclare(QUEUENAME, false, false, false, null);
        // 保证一次只分发一次 限制发送给同一个消费者 不得超过一条消息
        channel.basicQos(1);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    /** 手动回执消息 */
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
                System.out.println("X 消费者获取生产者消息：" + msg);
            }
        };
        // 消费者监听队列消息
        channel.basicConsume(QUEUENAME, false, consumer);
    }
}
