package icu.funkye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author funkye
 */
@EnableFeignClients
@SpringBootApplication
public class AccountServiceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceServiceApplication.class, args);
    }

}