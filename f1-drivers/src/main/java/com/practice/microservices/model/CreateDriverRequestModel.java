package com.practice.microservices.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateDriverRequestModel {

	@NotNull(message = "name cannot be null")
	@Size(min = 2, message = "name must be atlease 2 characters")
	private String name;
	
	@NotNull(message = "email cannot be null")
	@Email
	private String email;
	
	@NotNull(message = "password cannot be null")
	@Size(min = 8, max = 16, message = "password should be between 8 and 16 characters")
	private String password;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
