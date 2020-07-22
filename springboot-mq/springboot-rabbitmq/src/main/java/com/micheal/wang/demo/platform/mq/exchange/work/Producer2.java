package com.micheal.wang.demo.platform.mq.exchange.work;

import com.micheal.wang.demo.platform.mq.exchange.util.MQConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer2 {

    private final static String QUEUENAME = "test_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接
        Connection conn = MQConnectionUtils.connect();
        // 创建通道
        Channel channel = conn.createChannel();
        // 绑定的交换机 参数1交互机名称 参数2 exchange类型
        channel.queueDeclare(QUEUENAME, false, false, false, null);
        // 保证一次只分发一次 限制发送给同一个消费者 不得超过一条消息
        channel.basicQos(1);
        for (int i=1; i<=50; i++) {
            String msg = "生产者消息_" + i;
            System.out.println("生产者发送消息_" + msg);
            // 发送消息
            channel.basicPublish("", QUEUENAME, null, msg.getBytes());
        }
        // 关闭通道、连接
        channel.close();
        conn.close();
        // 注意：如果消费没有绑定交换机和队列，则消息会丢失
    }

}
