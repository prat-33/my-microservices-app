package com.practice.microservices.service;

import java.util.ArrayList;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.practice.microservices.dao.DriversRepo;
import com.practice.microservices.entity.DriverEntity;
import com.practice.microservices.shared.DriverDto;

@Service
public class DriverServiceImpl implements DriverService {

	private BCryptPasswordEncoder encoder;
	private DriversRepo repo;
	
	public DriverServiceImpl(DriversRepo repo, BCryptPasswordEncoder encoder) {
		this.repo = repo;
		this.encoder = encoder;
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

}
