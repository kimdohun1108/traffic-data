package com.example.demo.controller;

import com.example.demo.dto.CitydataDto; 
import com.example.demo.repository.CitydataRepository; 
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        System.out.println("🚀 데이터 수집 및 RDS 저장을 시작합니다...");

        for (String place : places) {
            String url = String.format("http://openapi.seoul.go.kr:8088/%s/xml/citydata/1/5/%s", apiKey, place);
            try {
                String xmlResponse = restTemplate.getForObject(url, String.class);
                CityData temporaryXmlData = xmlMapper.readValue(xmlResponse, CityData.class);

                // API로 받은 데이터를 DB에 저장할 TrafficData 객체로 변환합니다.
                CitydataDto citydataDto = new CitydataDto();
                citydataDto.setPlaceName(place);
                citydataDto.setAvgRoadIdx(temporaryXmlData.getRoadTrafficInfo().getRoadTrafficIdx());
                citydataDto.setAvgRoadSpeed(temporaryXmlData.getRoadTrafficInfo().getRoadTrafficSpd());
                citydataDto.setTemp(temporaryXmlData.getWeatherInfo().getTemp());
                citydataDto.setPrecipitation(temporaryXmlData.getWeatherInfo().getPrecipitation());
                citydataDto.setTimestamp(LocalDateTime.now());

                // 데이터베이스에 저장!
                citydataRepository.save(citydataDto);

                System.out.println("✅ [" + place + "] 데이터 RDS 저장 완료");
            } catch (Exception e) {
                System.out.println("❌ [" + place + "] 데이터 수집/저장 실패: " + e.getMessage());
            }
        }
        System.out.println("💾 모든 데이터의 RDS 저장이 완료되었습니다.");
    }
}
