package com.experiment.ipinfo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
class IpinfoApplicationTests {

	@Value("${ip-ranges-location}")
	private String location;

	@Test
	void contextLoads() {
	}

}
