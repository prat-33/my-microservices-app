package com.practice.microservices.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.practice.microservices.model.AlbumResponseModel;

@FeignClient(name = "f1-albums")
public interface AlbumsServiceClient {

	@GetMapping("/drivers/{driverId}/albums")
	public List<AlbumResponseModel> getAlbums(@PathVariable String driverId);
}
