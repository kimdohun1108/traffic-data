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

     @Column(nullable = false)
    private String hotspotName; 
    
    // 도로 시각화 데이터
    private String roadName; // 34. 도로명
    private String startXy; // 37. 시작점 좌표
    private String endXy; // 40. 종점 좌표
    
    // ML 데이터
    private String speed; // 42. 평균 속도
    private String temp; // 176. 기온
    private String windSpd; // 182. 풍속
    private String precipitation; // 183. 강수량
    private String precptType; // 184. 강수형태
    private String newsList; // 200. 기상특보
    private String accidentStatus; // 138. 사고통제현황

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
