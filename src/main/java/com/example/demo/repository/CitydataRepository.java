package com.example.demo.repository;

import com.example.demo.dto.CitydataDto;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository를 상속받기만 하면, 기본적인 DB 작업(저장, 조회, 삭제 등) 메서드를 자동으로 쓸 수 있습니다.
public interface CitydataRepository extends JpaRepository<CitydataDto, Integer>{

}
