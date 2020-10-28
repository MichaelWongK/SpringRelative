package com.security.oauth2server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class Oauth2ServerApplicationTests {

	@Test
	public void encodeSecret() {
		System.out.println(new BCryptPasswordEncoder().encode("secret"));
	}

}
