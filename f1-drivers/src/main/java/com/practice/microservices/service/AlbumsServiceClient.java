package com.practice.microservices.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.practice.microservices.model.AlbumResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "f1-albums")
public interface AlbumsServiceClient {

	@GetMapping("/drivers/{driverId}/albums")
	@CircuitBreaker(name = "f1-albums", fallbackMethod = "getAlbumsFallback")
	public List<AlbumResponseModel> getAlbums(@PathVariable String driverId);
	
	default List<AlbumResponseModel> getAlbumsFallback(String driverId, Throwable exception) {
		System.out.println("Param: " + driverId);
		System.out.println("Exception: " + exception.getMessage());
		return new ArrayList<>();
	}
}
