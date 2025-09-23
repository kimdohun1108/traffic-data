package com.example.demo.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity // "이 클래스는 데이터베이스 테이블의 설계도입니다"
@Table(name = "traffic_data") // 테이블 이름은 "traffic_data"로 만들어주세요
@Getter
@Setter
@NoArgsConstructor // JPA는 기본 생성자가 반드시 필요합니다.
public class CitydataDto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;

    @Column(nullable = false) // DB에 저장될 때 이 값은 비어있으면 안됩니다.
    private String placeName;
    private String avgRoadIdx;
    private String avgRoadSpeed;
    private String temp;
    private String precipitation;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
