package com.practice.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class F1EurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(F1EurekaApplication.class, args);
	}

}
