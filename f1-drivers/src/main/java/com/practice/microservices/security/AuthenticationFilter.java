package com.practice.microservices.security;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.microservices.model.LoginRequestModel;
import com.practice.microservices.service.DriverService;
import com.practice.microservices.shared.DriverDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private DriverService service;
	private Environment env;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager, Environment env, DriverService service) {
		super(authenticationManager);
		this.env = env;
		this.service = service;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
		try {
			LoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), LoginRequestModel.class);
			
			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(
							creds.getEmail(), 
							creds.getPassword(), 
							new ArrayList<>()));
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
		String username = ((User)auth.getPrincipal()).getUsername();
		DriverDto driverDto = service.getUserDetailsByEmail(username);
		String tokenSecret = env.getProperty("token.secret");
		byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
		
		Instant now = Instant.now();
		
		String token = Jwts.builder()
				.subject(driverDto.getUserId())
				.expiration(Date.from(now.plusMillis(Long.parseLong(env.getProperty("token.expiration_time")))))
				.issuedAt(Date.from(now))
				.signWith(secretKey)
				.compact();
		
		res.addHeader("token", token);
		res.addHeader("userId", driverDto.getUserId());
	}
}
