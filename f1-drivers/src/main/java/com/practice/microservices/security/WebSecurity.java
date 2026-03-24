package com.practice.microservices.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import com.practice.microservices.service.DriverService;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	private Environment environment;
	private DriverService service;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity(Environment environment, BCryptPasswordEncoder bCryptPasswordEncoder, DriverService service) {
		this.environment = environment;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.service = service;
	}
	
	@Bean
	protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
		
		authenticationManagerBuilder.userDetailsService(service).passwordEncoder(bCryptPasswordEncoder);
		
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, environment, service);
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		
		httpSecurity.csrf(csrf -> csrf.disable());
		
		httpSecurity.authorizeHttpRequests(auth -> auth.requestMatchers("/users/**")
				.access(new WebExpressionAuthorizationManager("hasIpAddress('" + environment.getProperty("gateway.ip") + "')"))
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/actuator/**").permitAll())
		.addFilter(authenticationFilter)
		.authenticationManager(authenticationManager)
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		httpSecurity.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
		return httpSecurity.build();
	}
	
	/*
	 * @Bean public SecurityFilterChain filterChain(HttpSecurity httpSecurity)
	 * throws Exception { httpSecurity.csrf(csrf -> csrf.disable());
	 * 
	 * httpSecurity.authorizeHttpRequests(auth -> auth.requestMatchers("/users/**")
	 * .access(new WebExpressionAuthorizationManager("hasIpAddress('" +
	 * environment.getProperty("gateway.ip") + "')"))
	 * .requestMatchers("/h2-console/**").permitAll()) .sessionManagement(session ->
	 * session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	 * 
	 * httpSecurity.headers(headers -> headers.frameOptions(frameOptions ->
	 * frameOptions.sameOrigin()));
	 * 
	 * return httpSecurity.build(); }
	 */
}
