package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.s3Report;

@Repository
public interface s3Repository extends JpaRepository<s3Report, Integer>{

	Optional<s3Report> findByBuildNumber(int id);


}
