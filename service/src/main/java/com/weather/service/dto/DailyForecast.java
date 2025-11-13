package com.weather.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyForecast {
    private String date;
    private Double highTemp;
    private Double lowTemp;
    private Double windSpeed;
    private boolean hasRain;
    private boolean hasThunderstorm;
    private List<String> recommendations;
}