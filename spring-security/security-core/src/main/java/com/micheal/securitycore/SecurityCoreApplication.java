package com.micheal.securitycore;

import com.micheal.securitycore.properties.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityCoreApplication.class, args);
    }

}
