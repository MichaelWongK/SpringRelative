package com.micheal.mqtt.controller;

import com.micheal.mqtt.message.MqttMessage;
import com.micheal.mqtt.send.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/4/13 10:38
 * @Description
 */
@RestController
public class MqttPubController {

    @Autowired
    private MqttGateway mqttGateway;

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody MqttMessage message) {
        mqttGateway.sendToMqtt(message.getTopic(), message.getContent());
        return "send topic: " + message.getTopic()+ ", message : " + message.getContent();
    }
}
