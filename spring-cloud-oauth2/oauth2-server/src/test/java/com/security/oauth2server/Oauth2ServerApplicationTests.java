package com.security.oauth2server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Oauth2ServerApplicationTests {

	@Test
	public void encodeSecret() {
		System.out.println(new BCryptPasswordEncoder().encode("secret"));
	}

}
