package com.micheal.ouath2ssoclienttwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

@EnableOAuth2Sso
@SpringBootApplication
public class Ouath2SsoClientTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ouath2SsoClientTwoApplication.class, args);
    }

}
