package com.micheal.wang.demo.platform.mq.exchange.direct;

import com.micheal.wang.demo.platform.mq.exchange.util.MQConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class ProducerRouting {

    private final static String EXCHANGE_NAME = "my_exchange_direct";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接
        Connection conn = MQConnectionUtils.connect();
        // 创建通道
        Channel channel = conn.createChannel();
        // 绑定的交换机 参数1交互机名称 参数2 exchange类型
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String msg = "", sendType = "";
        for (int i=0; i<10; i++) {
            if (i%2 == 0) {
                sendType = "info";
                msg = "我是 info 级别的消息类型：" + i;
            } else {
                sendType = "error";
                msg = "我是 error 级别的消息类型：" + i;
            }
            // 将消息发送至exchange
            // 第二个参数为空类似于表示全局广播，只要绑定到该队列上的消费者理论上是都可以收到的。
            channel.basicPublish(EXCHANGE_NAME, sendType, null, msg.getBytes());
            System.out.println("[send]：" + msg + "  " +sendType);
        }
        // 关闭通道、连接
        channel.close();
        conn.close();
        // 注意：如果消费没有绑定交换机和队列，则消息会丢失
    }

}
