package com.practice.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class F1SpringCloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(F1SpringCloudConfigServerApplication.class, args);
	}

}
