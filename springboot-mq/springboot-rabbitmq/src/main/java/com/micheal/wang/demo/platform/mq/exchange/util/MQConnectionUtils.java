package com.micheal.wang.demo.platform.mq.exchange.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQConnectionUtils {
    private final static String HOST = "47.100.93.46";
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static Integer PORT = 5672;
    private final static String VIRTUAL_HOST = "/";

    /**
     * 创建新链接
     */
    public static Connection connect() throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置连接参数
        factory.setHost(HOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setPort(PORT);
        factory.setVirtualHost(VIRTUAL_HOST);

        return factory.newConnection();
    }
}
