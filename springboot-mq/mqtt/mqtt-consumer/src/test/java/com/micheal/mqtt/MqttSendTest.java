//package com.micheal.mqtt;
//
//import com.micheal.mqtt.message.MqttMessage;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
// * @date 2023/4/12 16:36
// * @Description
// */
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class MqttSendTest {
//
//    @Autowired
//    private MqttGateway mqttGateway;
//
//    @Test
//    public void send() {
//        MqttMessage message = new MqttMessage();
//        message.setTopic("testtopic/data");
//        message.setContent("你好啊");
//        mqttGateway.sendToMqtt(message.getTopic(), message.getContent());
//    }
//}
