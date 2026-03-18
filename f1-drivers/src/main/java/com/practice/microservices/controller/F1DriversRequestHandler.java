package com.practice.microservices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class F1DriversRequestHandler {

	@GetMapping("/home")
	public String getHome() {
		return "WELCOME F1 DRIVER !";
	}
}
