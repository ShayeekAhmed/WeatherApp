package com.weather.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherMapResponse {
    private List<WeatherData> list;
    private City city;

    @Data
    public static class WeatherData {
        private Main main;
        private List<Weather> weather;
        private Wind wind;
        @JsonProperty("dt_txt")
        private String dtTxt;
    }

    @Data
    public static class Main {
        private Double temp;
        @JsonProperty("temp_min")
        private Double tempMin;
        @JsonProperty("temp_max")
        private Double tempMax;
        private Double humidity;
    }

    @Data
    public static class Weather {
        private String main;
        private String description;
    }

    @Data
    public static class Wind {
        private Double speed;
        private Double deg;
    }

    @Data
    public static class City {
        private String name;
        private String country;
    }
}