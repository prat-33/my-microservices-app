package com.practice.microservices.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.practice.microservices.entity.DriverEntity;

@Repository
public interface DriversRepo extends CrudRepository<DriverEntity, Integer> {

	DriverEntity findByEmail(String email);
}
