package com.practice.microservices.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.practice.microservices.shared.DriverDto;

public interface DriverService extends UserDetailsService {

	DriverDto createUser(DriverDto driverDetails);
	DriverDto getUserDetailsByEmail(String email);
}
