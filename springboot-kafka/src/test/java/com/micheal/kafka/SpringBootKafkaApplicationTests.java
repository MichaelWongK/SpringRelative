package com.micheal.kafka;

import com.micheal.kafka.provider.KafkaProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2021/6/30 9:07
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringBootKafkaApplicationTests {

    @Autowired
    private KafkaProvider kafkaProvider;

    @Test
    public void sendMessage() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            kafkaProvider.sendMessage(i + 1, UUID.randomUUID().toString(), LocalDateTime.now());
        }

        TimeUnit.MINUTES.sleep(1);
    }
}
