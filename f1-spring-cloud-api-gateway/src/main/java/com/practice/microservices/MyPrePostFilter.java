package com.practice.microservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

@Configuration
public class MyPrePostFilter {

	final Logger logger = LoggerFactory.getLogger(MyPrePostFilter.class);
	
	@Bean
	@Order(2)
	public GlobalFilter thirdFilter() {
		return (exchange, chain) -> {
			logger.info("My third Pre Filter executed");
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("My third Post Filter executed");
			}));
		};
	}
	
	@Bean
	@Order(1)
	public GlobalFilter secondFilter() {
		return (exchange, chain) -> {
			logger.info("My second Pre Filter executed");
			
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("My second Post Filter executed");
			}));
		};
	}
}
