package com.practice.microservices.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.practice.microservices.dao.DriversRepo;
import com.practice.microservices.entity.DriverEntity;
import com.practice.microservices.model.AlbumResponseModel;
import com.practice.microservices.shared.DriverDto;

import feign.FeignException;

@Service
public class DriverServiceImpl implements DriverService {

	final static Logger logger = LoggerFactory.getLogger(DriverServiceImpl.class);
	private BCryptPasswordEncoder encoder;
	private DriversRepo repo;
	private RestTemplate restTemplate;
	private AlbumsServiceClient client;
	
	public DriverServiceImpl(DriversRepo repo, BCryptPasswordEncoder encoder, RestTemplate restTemplate, AlbumsServiceClient client) {
		this.repo = repo;
		this.encoder = encoder;
		this.restTemplate = restTemplate;
		this.client = client;
	}
	
	@Override
	public DriverDto createUser(DriverDto driverDetails) {
		driverDetails.setUserId(UUID.randomUUID().toString());
		driverDetails.setEncryptedPassword(encoder.encode(driverDetails.getPassword()));
		
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		DriverEntity entity = mapper.map(driverDetails, DriverEntity.class);
		repo.save(entity);
		
		DriverDto returnVal = mapper.map(entity, DriverDto.class);
		
		return returnVal;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DriverEntity driverEntity = repo.findByEmail(username);
		
		if(driverEntity == null) {
			throw new UsernameNotFoundException(username);
		}
		
		return new User(driverEntity.getEmail(), driverEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	@Override
	public DriverDto getUserDetailsByEmail(String email) {
		DriverEntity driverEntity = repo.findByEmail(email);
		
		if(driverEntity == null) {
			throw new UsernameNotFoundException(email);
		}
		
		return new ModelMapper().map(driverEntity, DriverDto.class);
	}

	@Override
	public DriverDto getUserByDriverId(String driverId) {
		DriverEntity driverEntity = repo.findByUserId(driverId);
		
		if(driverEntity == null) {
			throw new UsernameNotFoundException(driverId);
		}
		
		DriverDto userDto = new ModelMapper().map(driverEntity, DriverDto.class);
		
		/* COMMENTED TO USE FeignClient for microservice communications which is declarative instead of RestTemplate
		 * String albumUrl = String.format("//F1-ALBUMS/drivers/%s/albums", driverId);
		 * 
		 * ResponseEntity<List<AlbumResponseModel>> albumListResponse =
		 * restTemplate.exchange(albumUrl, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<List<AlbumResponseModel>>() {
		 * 
		 * }); List<AlbumResponseModel> albumList = albumListResponse.getBody();
		 */
		
		List<AlbumResponseModel> albumList = null;
		
		try {
			albumList = client.getAlbums(driverId);
		}
		catch(FeignException e) {
			logger.error(e.getLocalizedMessage());
		}
		
		userDto.setAlbums(albumList);
		
		return userDto;
	}
}
