package com.practice.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class F1DriversApplication {

	public static void main(String[] args) {
		SpringApplication.run(F1DriversApplication.class, args);
	}

}
