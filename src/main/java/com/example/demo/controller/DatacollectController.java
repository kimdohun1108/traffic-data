package com.example.demo.controller;

import com.example.demo.dto.CitydataDto; 
import com.example.demo.dto.Citydata; 
import com.example.demo.dto.Citydata.RoadLinkStatus;
import com.example.demo.repository.CitydataRepository; 
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URI;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatacollectController implements CommandLineRunner{
    
    @Value("${seoul.api.key}")
    private String apiKey;
    @Value("${seoul.api.places}")
    private String[] places;

     // 스프링이 자동으로 데이터베이스 관리인을 여기에 주입(연결)해줍니다.
    @Autowired
    private CitydataRepository citydataRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 [Extract] 120개 핫스팟의 상세 도로/날씨/사고 데이터 수집을 시작합니다...");

        for (String place : places) {
             URI uri = UriComponentsBuilder
                    .fromUriString("http://openapi.seoul.go.kr:8088")
                    .path("/{apiKey}/xml/citydata/1/1000/{placeName}")
                    .buildAndExpand(apiKey, place) // 변수를 안전하게 인코딩하며 채워넣음
                    .toUri();

            try {
                String xmlResponse = restTemplate.getForObject(url, String.class);
                Citydata temporaryXmlData = xmlMapper.readValue(xmlResponse, Citydata.class);

                List<RoadLinkStatus> roadStatusList = temporaryXmlData.getRoadTrafficStatus();
                if (roadStatusList != null) {
                    for (RoadLinkStatus roadStatus : roadStatusList) {
                        // DB에 저장할 최종 보고서(CitydataDto)를 준비합니다.
                        CitydataDto citydataDto = new CitydataDto();
                        // 장소
                        citydataDto.setHotspotName(place);
                        
                        // --- 도로 시각화 데이터 ---
                        citydataDto.setRoadName(roadStatus.getRoadName());
                        citydataDto.setStartXy(roadStatus.getStartNdXy());
                        citydataDto.setEndXy(roadStatus.getEndNdXy());

                        // --- ML 데이터 ---
                        citydataDto.setSpeed(roadStatus.getSpd());
                        if(temporaryXmlData.getWeatherInfo() != null){
                            citydataDto.setTemp(temporaryXmlData.getWeatherInfo().getTemp());
                            citydataDto.setWindSpd(temporaryXmlData.getWeatherInfo().getWindSpd());
                            citydataDto.setPrecipitation(temporaryXmlData.getWeatherInfo().getPrecipitation());
                            citydataDto.setPrecptType(temporaryXmlData.getWeatherInfo().getPrecptType());
                            citydataDto.setNewsList(temporaryXmlData.getWeatherInfo().getNewsList());
                        }
                        if(temporaryXmlData.getAccidentInfo() != null){
                            citydataDto.setAccidentStatus(temporaryXmlData.getAccidentInfo().getStatus());
                        }
                        
                        citydataDto.setTimestamp(LocalDateTime.now());
                        
                        // 완성된 CitydataDto 객체를 DB에 저장합니다.
                        citydataRepository.save(citydataDto);
                    }
                }
                System.out.println("✅ [" + place + "] 주변 " + (roadStatusList != null ? roadStatusList.size() : 0) + "개 도로 구간 데이터 저장 완료");
            } catch (Exception e) {
                System.out.println("❌ [" + place + "] 데이터 수집/저장 실패: " + e.getMessage());
            }
        }
        System.out.println("💾 [Extract] 모든 데이터의 RDS 저장이 완료되었습니다.");
    }
}
