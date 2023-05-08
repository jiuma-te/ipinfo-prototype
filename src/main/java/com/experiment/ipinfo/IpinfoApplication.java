package com.experiment.ipinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IpinfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpinfoApplication.class, args);
	}

}
