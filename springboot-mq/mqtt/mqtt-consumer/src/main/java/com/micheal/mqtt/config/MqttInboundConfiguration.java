package com.micheal.mqtt.config;

import com.micheal.mqtt.property.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;


/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/4/13 8:45
 * @Description
 */
@Configuration
public class MqttInboundConfiguration {

    @Autowired
    private MqttProperties mqttProperties;

//    @Bean
//    public MqttPahoClientFactory mqttClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setServerURIs(new String[] {mqttProperties.getUrl()});
//        options.setUserName(mqttProperties.getUsername());
//        options.setPassword(mqttProperties.getPassword().toCharArray());
//        // 接收离线消息
//        //告诉代理客户端是否要建立持久会话   false为建立持久会话
//        options.setCleanSession(false);
//        options.setAutomaticReconnect(true);
//        options.setConnectionTimeout(30000);
//
//        factory.setConnectionOptions(options);
//        return factory;
//    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getUrl(), mqttProperties.getClientId() + System.currentTimeMillis(), mqttProperties.getDefaultTopic(), "micheal");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println("message:"+message);
                System.out.println("----------------------");
                System.out.println("message:"+message.getPayload());
                System.out.println("PacketId:"+message.getHeaders().getId());
                System.out.println("Qos:"+message.getHeaders().get(MqttHeaders.QOS));

                String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
                System.out.println("topic:"+topic);
            }
        };
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

}
