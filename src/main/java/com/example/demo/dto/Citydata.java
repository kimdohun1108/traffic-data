package com.example.demo.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// 이 클래스는 오직 API의 XML 구조를 받아내기 위한 임시 그릇(주문 메모)입니다.
@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "SeoulRtd.citydata")
public class Citydata {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ROAD_TRAFFIC_STTS")
    private List<RoadLinkStatus> roadTrafficStatus;

    @JacksonXmlProperty(localName = "WEATHER_STTS")
    private WeatherInfo weatherInfo;

    @JacksonXmlProperty(localName = "ACDNT_CNTRL_STTS")
    private AccidentInfo accidentInfo;

    // 아래 클래스들은 CityData 안에서만 사용되는 보조 그릇들입니다.
    @Data
    @NoArgsConstructor
    public class RoadLinkStatus {
        @JacksonXmlProperty(localName = "ROAD_NM")
        private String roadName; // 34. 도로명
        @JacksonXmlProperty(localName = "START_ND_XY")
        private String startNdXy; // 37. 시작점 좌표
        @JacksonXmlProperty(localName = "END_ND_XY")
        private String endNdXy; // 40. 종점 좌표
        @JacksonXmlProperty(localName = "SPD")
        private String spd; // 42. 평균 속도
    }

    @Data
    @NoArgsConstructor
    public class WeatherInfo {
        @JacksonXmlProperty(localName = "TEMP")
        private String temp; // 176. 기온
        @JacksonXmlProperty(localName = "WIND_SPD")
        private String windSpd; // 182. 풍속
        @JacksonXmlProperty(localName = "PRECIPITATION")
        private String precipitation; // 183. 강수량
        @JacksonXmlProperty(localName = "PRECPT_TYPE")
        private String precptType; // 184. 강수형태
        @JacksonXmlProperty(localName = "NEWS_LIST")
        private String newsList; // 200. 기상특보
    }

    @Data
    @NoArgsConstructor
    public class AccidentInfo {
        @JacksonXmlProperty(localName = "ACDNT_TYPE")
        private String status; // 138. 사고통제현황
    }
}