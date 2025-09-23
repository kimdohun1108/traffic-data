package com.example.demo.repository;

import com.example.demo.dto.CitydataDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitydataRepository extends JpaRepository<CitydataDto, Integer>{

}
