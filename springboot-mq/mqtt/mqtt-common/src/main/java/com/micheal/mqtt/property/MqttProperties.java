package com.micheal.mqtt.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/4/13 8:45
 * @Description
 */
@Component
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperties {
    private String username;
    private String password;
    private String url;
    private String clientId;
    private String defaultTopic;
    private String timeout;
    private String keepalive;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(String keepalive) {
        this.keepalive = keepalive;
    }
}
