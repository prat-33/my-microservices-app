package com.practice.microservices.security;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	private Environment env;
	
	public static class Config {
		
	}
	
	public AuthorizationHeaderFilter(Environment env) {
		super(Config.class);
		this.env = env;
	}
	
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			
			if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "").trim();
			
			if(!isJwtValid(jwt)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}
			
			return chain.filter(exchange);
		};
	}
	
	private Mono<Void> onError(ServerWebExchange exhange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exhange.getResponse();
		response.setStatusCode(httpStatus);
		return  response.setComplete();
	}
	
	private boolean isJwtValid(String jwt) {
		boolean returnVal = true;
		
		String subject = null;
		
		try {
            byte[] secretKeyBytes = Base64.getEncoder().encode(env.getProperty("token.secret").getBytes());
            SecretKey key = Keys.hmacShaKeyFor(secretKeyBytes);
			JwtParser parser = Jwts.parser()
                    .verifyWith(key)
                    .build();
            subject = parser.parseSignedClaims(jwt).getPayload().getSubject();
        } catch (Exception ex) {
            returnVal = false;
        }
		
		if(subject == null || subject.isEmpty()) {
			returnVal = false;
		}
		
		return returnVal;
	}
}
