package com.practice.microservices.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.microservices.model.CreateDriverRequestModel;
import com.practice.microservices.model.CreateDriverResponseModel;
import com.practice.microservices.model.DriverResponseModel;
import com.practice.microservices.service.DriverService;
import com.practice.microservices.shared.DriverDto;

@RestController
@RequestMapping("/users")
public class F1DriversRequestHandler {

	private Environment env;
	private DriverService service;
	
	public F1DriversRequestHandler(Environment env, DriverService service) {
		this.env = env;
		this.service = service;
	}
	
	@GetMapping("/home")
	public String getHome() {
		return "WELCOME F1 DRIVER !";
	}
	
	@PostMapping("/drivers")
	public ResponseEntity<CreateDriverResponseModel> createDriver(@RequestBody CreateDriverRequestModel driverRequest) {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		DriverDto dto = mapper.map(driverRequest, DriverDto.class);
		
		DriverDto createdDriver = service.createUser(dto);
		
		CreateDriverResponseModel response = mapper.map(createdDriver, CreateDriverResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping(value = "/{driverId}")
	public ResponseEntity<DriverResponseModel> getDriver(@PathVariable String driverId) {
		DriverDto driverDto = service.getUserByDriverId(driverId);
		
		DriverResponseModel driverResponseModel = new ModelMapper().map(driverDto, DriverResponseModel.class);
		return ResponseEntity.status(HttpStatus.OK).body(driverResponseModel);
	}
}
